package com.example.intelligentbibackend.controller;


import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.intelligentbibackend.common.BaseResponse;
import com.example.intelligentbibackend.common.ErrorCode;
import com.example.intelligentbibackend.common.ResultUtils;

import com.example.intelligentbibackend.constant.CommonConstant;
import com.example.intelligentbibackend.exception.BesinessException;
import com.example.intelligentbibackend.manager.AiManager;
import com.example.intelligentbibackend.manager.RedisLimiterManager;
import com.example.intelligentbibackend.model.domain.Chart;
import com.example.intelligentbibackend.model.domain.User;
import com.example.intelligentbibackend.model.request.chart.ChartQueryRequest;
import com.example.intelligentbibackend.model.request.chart.GenChartByAiRequest;
import com.example.intelligentbibackend.model.vo.BiResponse;
import com.example.intelligentbibackend.service.ChartService;
import com.example.intelligentbibackend.service.UserService;
import com.example.intelligentbibackend.utils.ExcelUtils;
import com.example.intelligentbibackend.utils.SqlUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/chart")
@CrossOrigin(origins = {"http://localhost:5173"},allowCredentials = "true")
@Slf4j
public class ChartController {

    @Resource
    private UserService userService;
    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;
    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 图表生成(同步)
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */

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
//            校验文件
        long size = multipartFile.getSize();
        String filename = multipartFile.getOriginalFilename();
        final long ONE_SIZE = 1024 * 1024 ;
//        文件不能超过1M
        if (size > ONE_SIZE){
            throw new BesinessException(ErrorCode.PARAMS_ERROR, "文件过大");
        }
//        校验文件后缀
        String validFileSuffixList = FileUtil.getSuffix(filename);
//        文件白名单（允许用的文件类型）
        List<String> list = Arrays.asList("xls", "xlsx","csv");
        if(!list.contains(validFileSuffixList)){
            throw new BesinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
        }
//        限流判断,每个用户一个限流器
        redisLimiterManager.doRateLimiter("genChartByAI"+String.valueOf(userId));


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
        chart.setStatus("succeed");
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


    }

    /**
     * 图表AI生成(异步处理)
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */

    @PostMapping("/gen/async")
    public BaseResponse<BiResponse> genChartByAiAsync(@RequestPart("file") MultipartFile multipartFile,
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
//            校验文件
        long size = multipartFile.getSize();
        String filename = multipartFile.getOriginalFilename();
        final long ONE_SIZE = 1024 * 1024 ;
//        文件不能超过1M
        if (size > ONE_SIZE){
            throw new BesinessException(ErrorCode.PARAMS_ERROR, "文件过大");
        }
//        校验文件后缀
        String validFileSuffixList = FileUtil.getSuffix(filename);
//        文件白名单（允许用的文件类型）
        List<String> list = Arrays.asList("xls", "xlsx","csv");
        if(!list.contains(validFileSuffixList)){
            throw new BesinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
        }
//        限流判断,每个用户一个限流器
        redisLimiterManager.doRateLimiter("genChartByAI"+String.valueOf(userId));


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


//        插入数据库
        Chart chart = new Chart();
        chart.setChartName(chartName);
        chart.setGoal(goal);
        chart.setChartType(chartType);
        chart.setChartData(csvData);
        chart.setStatus("wait");

        chart.setUserId(userId);
        boolean save = chartService.save(chart);
        if (!save){
            throw new BesinessException(ErrorCode.SYSTEM_ERROR, "保存失败");
        }
//        todo:进程满了之后给个异常报错
        CompletableFuture.runAsync(() -> {
            Chart updateChart = new Chart();
            updateChart.setId(chart.getId());
            updateChart.setStatus("running");
            boolean b = chartService.updateById(updateChart);
            if (!b){
                handleChartUpdateError(chart.getId(),"更新图表执行中状态失败");
                return;
            }
//             ai生成
            String gen = aiManager.doSyncStableRequest(prompt, userInput.toString());
            String[] splits = gen.split("【【【【【");
            if (splits.length < 3) {
                handleChartUpdateError(chart.getId(),"AI 生成错误");
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
            if (!result){
                handleChartUpdateError(chart.getId(),"图表更新成功状态失败");
            }
        },threadPoolExecutor);

        //        将返回结果统一封装在一个类里面
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);

    }

    /**
     * 图表错误信息类
     * @param chartId
     * @param execMessage
     */
    private void handleChartUpdateError(long chartId,String execMessage){
        Chart updateChart = new Chart();
        updateChart.setId(chartId);
        updateChart.setStatus("failed");
        updateChart.setExecMessage(execMessage);
        boolean b = chartService.updateById(updateChart);
        if (!b){
           log.info("图表更新failed状态失败"+chartId+","+execMessage);
        }
    }

    /**
     * 查询图表（分页）
     * @param chartQueryRequest
     * @param request
     * @return
     */

    @PostMapping("/my/list/page")
    public BaseResponse<Page<Chart>> listMyChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                       HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BesinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getloginuser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        if (size>20){
            throw new BesinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }



    /**
     * 获取查询包装类
     *
     * @param chartQueryRequest
     * @return
     */
    private QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(name), "chartName", name);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}
