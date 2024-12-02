package com.example.intelligentbibackend.model.request.chart;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class GenChartByAiRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -4477620167026391836L;
    /**
     * 分析目标
     */
    private String goal;
    /**
     * 图表名字
     */
    private String chartName;
    /**
     * 图表类型
     */
    private String chartType;
}
