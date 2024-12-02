package com.example.intelligentbibackend.model.request.chart;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 查询图表
 */

@Data
public class ChartQueryRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -472328972874752720L;

   
    private Long id;

    /**
     * 分析目标
     */
    private String goal;


    /**
     * 图表类型
     */
    private String chartType;
    /**
     * userID
     */
    private Long userId;



}
