package com.example.intelligentbibackend.model.request.chart;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 编辑图表
 */

@Data
public class ChartEditRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -472328972874752720L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

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
