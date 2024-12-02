package com.example.intelligentbibackend.model.vo;

import lombok.Data;

/**
 * 返回给前端的规范类
 */
@Data
public class BiResponse {

    /**
     * AI生成图表数据
     */
    private String aiGenChart;

    /**
     * 生成的分析结论
     */
    private String aiGenResult;
    /**
     * 生成的图表id
     */

    private Long chartId;
}