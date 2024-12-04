package com.example.intelligentbibackend.model.request.chart;


import com.example.intelligentbibackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 查询图表
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -472328972874752720L;

   
    private Long id;
    /**
     * 名称
     */
    private String name;


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