package com.example.intelligentbibackend.mq;

import com.example.intelligentbibackend.common.ErrorCode;
import com.example.intelligentbibackend.constant.BiMqConstant;
import com.example.intelligentbibackend.exception.BesinessException;
import com.example.intelligentbibackend.manager.AiManager;
import com.example.intelligentbibackend.model.domain.Chart;
import com.example.intelligentbibackend.service.ChartService;
import com.example.intelligentbibackend.utils.ExcelUtils;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;


/**
 * 消费者代码
 */
@Component
// 使用@Slf4j注解生成日志记录器
@Slf4j
public class BIMessageConsumer {

    @Resource
    private ChartService chartService;
    @Resource
    private AiManager aiManager;


    /**
     * 接收消息的方法
     *
     * @param message     接收到的消息内容，是一个字符串类型
     * @param channel     消息所在的通道，可以通过该通道与 RabbitMQ 进行交互，例如手动确认消息、拒绝消息等
     * @param deliveryTag 消息的投递标签，用于唯一标识一条消息
     */
    // 使用@SneakyThrows注解简化异常处理
    @SneakyThrows
    // 使用@RabbitListener注解指定要监听的队列名称为"bi_queue"，并设置消息的确认机制为手动确认
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    // @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag是一个方法参数注解,用于从消息头中获取投递标签(deliveryTag),
    // 在RabbitMQ中,每条消息都会被分配一个唯一的投递标签，用于标识该消息在通道中的投递状态和顺序。通过使用@Header(AmqpHeaders.DELIVERY_TAG)注解,可以从消息头中提取出该投递标签,并将其赋值给long deliveryTag参数。
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        if (StringUtils.isBlank(message)) {
//            如果队列里的消息为空,就清除这条队列(消息拒绝),抛异常
            channel.basicNack(deliveryTag, false, false);
            throw new BesinessException(ErrorCode.SYSTEM_ERROR,"消息为空");
        }
//       拿到消息队列里的图表id，因为我们在controller传的就是图表id
        Long chartId = Long.parseLong(message);
//        然后通过这个id那到数据库里的数据
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            channel.basicNack(deliveryTag, false, false);
            throw new BesinessException(ErrorCode.NULL_ERROR,"图表不存在");
        }
        final  String prompt ="你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                "分析需求：\n" +
                "{数据分析的需求或者目标}\n" +
                "原始数据：\n" +
                "{csv格式的原始数据，用,作为分隔符}\n" +
                "请根据这两部分内容，严格按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）同时不要使用这个符号 '】'\n" +
                "'【【【【【'\n" +
                "{前端 Echarts V5 的 option 配置对象 JSON 代码, 不要生成任何多余的内容，比如注释和代码块标记}\n" +
                "'【【【【【'\n" +
                "{明确的数据分析结论、越详细越好，不要生成多余的注释} \n" +
                "下面是一个具体的例子的模板：\n" +
                "'【【【【【'\n" +
                "JSON格式代码\n" +
                "'【【【【【'\n" +
                "结论：\n";


        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus("running");
        boolean b = chartService.updateById(updateChart);
        if (!b) {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
            return;
        }
//             ai生成
        String gen = aiManager.doSyncStableRequest(prompt,buildUserInput(chart));
        String[] splits = gen.split("【【【【【");
        if (splits.length < 3) {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "AI 生成错误");
            return;
        }
//           生成的图表
        String genChart = splits[1].trim();
//        生成的分析结论
        String genResult = splits[2].trim();
//            AI执行完再改一次将生成的结果保存到数据库
        Chart ChartResult = new Chart();
        ChartResult.setId(chart.getId());
        ChartResult.setAiGenChart(genChart);
        ChartResult.setAiGenResult(genResult);
        ChartResult.setStatus("succeed");
        boolean result = chartService.updateById(ChartResult);
        if (!result) {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "图表更新成功状态失败");
        }
        // 使用日志记录器打印接收到的消息内容
        log.info("receiveMessage message = {}", message);
        // 投递标签是一个数字标识,它在消息消费者接收到消息后用于向RabbitMQ确认消息的处理状态。通过将投递标签传递给channel.basicAck(deliveryTag, false)方法,可以告知RabbitMQ该消息已经成功处理,可以进行确认和从队列中删除。
        // 手动确认消息的接收，向RabbitMQ发送确认消息
        channel.basicAck(deliveryTag, false);
    }


    /**
     *  用户提示词
     */
     private String buildUserInput(Chart chart){
         //        user_prompt预设
//        拿到数据后拼接这些数据和加上提示词 换行，这样便于AI更好的识别
         StringBuilder userInput = new StringBuilder();
         userInput.append("分析需求：").append("\n");
         String userGoal =  chart.getGoal();
         String chartType = chart.getChartType();
         String csvData = chart.getChartData();
         // 拼接分析目标
         if (StringUtils.isNotBlank(chartType)) {
             userGoal += "，请使用" + chartType;
         }
         userInput.append(userGoal).append("\n");

         userInput.append("原始数据：").append(csvData).append("\n");


         return userInput.toString();
     }

    /**
     * 图表错误信息类
     *
     * @param chartId
     * @param execMessage
     */

    private void handleChartUpdateError(long chartId, String execMessage) {
        Chart updateChart = new Chart();
        updateChart.setId(chartId);
        updateChart.setStatus("failed");
        updateChart.setExecMessage(execMessage);
        boolean b = chartService.updateById(updateChart);
        if (!b) {
            log.info("图表更新failed状态失败" + chartId + "," + execMessage);


        }

    }
}
