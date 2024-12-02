package com.example.intelligentbibackend.model.request.chart;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 前端传过来的数据
 */

@Data
public class ChartAddRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -472328972874752720L;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表数据
     */
    private String chartData;

    /**
     * 图表类型
     */
    private String chartType;


}
