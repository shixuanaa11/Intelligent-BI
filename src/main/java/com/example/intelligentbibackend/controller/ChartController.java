package com.example.intelligentbibackend.controller;


import com.example.intelligentbibackend.common.BaseResponse;
import com.example.intelligentbibackend.common.ErrorCode;
import com.example.intelligentbibackend.common.ResultUtils;
import com.example.intelligentbibackend.exception.BesinessException;
import com.example.intelligentbibackend.manager.AiManager;
import com.example.intelligentbibackend.model.domain.Chart;
import com.example.intelligentbibackend.model.domain.User;
import com.example.intelligentbibackend.model.request.chart.GenChartByAiRequest;
import com.example.intelligentbibackend.model.vo.BiResponse;
import com.example.intelligentbibackend.service.ChartService;
import com.example.intelligentbibackend.service.UserService;
import com.example.intelligentbibackend.utils.ExcelUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/chart")
@CrossOrigin(origins = {"http://localhost:5178"},allowCredentials = "true")
public class ChartController {

    @Resource
    private UserService userService;
    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    @PostMapping("/gen")
    public BaseResponse<BiResponse> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                                  GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {

        String chartName = genChartByAiRequest.getChartName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
                // 校验
        //        拿到用户登录id
        User loginUser = userService.getloginuser(request);
        if (loginUser==null){
            throw new BesinessException(ErrorCode.NO_LOGIN);
        }
        Long userId = loginUser.getId();
        if (StringUtils.isBlank(goal)){
            throw new BesinessException(ErrorCode.PARAMS_ERROR, "目标为空");
        }
        if (StringUtils.isNotBlank(chartName) && chartName.length() > 100){
            throw new BesinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }

//        user_prompt预设
//        拿到数据后拼接这些数据和加上提示词 换行，这样便于AI更好的识别
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        userInput.append("原始数据：").append(csvData).append("\n");



//      system_prompt预设
//        final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
//                "分析需求：\n" +
//                "{数据分析的需求或者目标}\n" +
//                "原始数据：\n" +
//                "{csv格式的原始数据，用,作为分隔符}\n" +
//                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
//                "【【【【【\n" +
//                "{前端 可视化工具 ANTV的G2的v5.2.2版本的 option 配置对象JSON代码，合理准确地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
//                "【【【【【\n" +
//                "{明确的数据分析结论、越详细越好，不要生成多余的注释}";
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
                "结论：";

//        ai生成
        String gen = aiManager.doSyncStableRequest(prompt, userInput.toString());
        String[] splits = gen.split("【【【【【");
        if (splits.length < 3) {
            throw new BesinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
        }
//        生成的图表
        String genChart = splits[1].trim();
//        生成的分析结论
        String genResult = splits[2].trim();
        System.out.println(gen);

//        插入数据库
        Chart chart = new Chart();
        chart.setChartName(chartName);
        chart.setGoal(goal);
        chart.setChartType(chartType);
        chart.setChartData(csvData);
        chart.setAiGenChart(genChart);
        chart.setAiGenResult(genResult);
        chart.setUserId(userId);
        boolean save = chartService.save(chart);
        if (!save){
            throw new BesinessException(ErrorCode.SYSTEM_ERROR, "保存失败");
        }
//        将返回结果统一封装在一个类里面
        BiResponse biResponse = new BiResponse();
        biResponse.setAiGenChart(genChart);
        biResponse.setAiGenResult(genResult);
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);


//        // 校验文件
//        long size = multipartFile.getSize();
//        String originalFilename = multipartFile.getOriginalFilename();
//        // 校验文件大小
//        final long ONE_MB = 1024 * 1024L;
//        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过 1M");
//        // 校验文件后缀 aaa.png
//        String suffix = FileUtil.getSuffix(originalFilename);
//        final List<String> validFileSuffixList = Arrays.asList("xlsx");
//        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");
//
//        User loginUser = userService.getloginuser(request);
//        // 限流判断，每个用户一个限流器
//        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());
//        // 无需写 prompt，直接调用现有模型，https://www.yucongming.com，公众号搜【鱼聪明AI】

//        long biModelId = CommonConstant.BI_MODEL_ID;
//        // 分析需求：
//        // 分析网站用户的增长情况
//        // 原始数据：
//        // 日期,用户数
//        // 1号,10
//        // 2号,20
//        // 3号,30
//
//        // 构造用户输入
//        StringBuilder userInput = new StringBuilder();
//        userInput.append("分析需求：").append("\n");
//
//        // 拼接分析目标
//        String userGoal = goal;
//        if (StringUtils.isNotBlank(chartType)) {
//            userGoal += "，请使用" + chartType;
//        }
//        userInput.append(userGoal).append("\n");
//        userInput.append("原始数据：").append("\n");
//        // 压缩后的数据
//        String csvData = ExcelUtils.excelToCsv(multipartFile);
//        userInput.append(csvData).append("\n");
//
//        String result = aiManager.doChat(biModelId, userInput.toString());
//        String[] splits = result.split("【【【【【");
//        if (splits.length < 3) {
//            throw new BesinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
//        }
//        String genChart = splits[1].trim();
//        String genResult = splits[2].trim();
//        // 插入到数据库
//        Chart chart = new Chart();
//        chart.setChartName(name);
//        chart.setGoal(goal);
//        chart.setChartData(csvData);
//        chart.setChartType(chartType);
//        chart.setAiGenChart(genChart);
//        chart.setAiGenChart(genResult);
//        chart.setUserId(loginUser.getId());
//        boolean saveResult = chartService.save(chart);
//        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");
//        BiResponse biResponse = new BiResponse();
//        biResponse.setGenChart(genChart);
//        biResponse.setGenResult(genResult);
//        biResponse.setChartId(chart.getId());
//        return ResultUtils.success(biResponse);
    }
}
