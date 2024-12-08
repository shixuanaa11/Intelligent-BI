package com.example.intelligentbibackend.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.Data;

/**
 * 图表表
 * @TableName chart
 */
@TableName(value ="chart")
@Data
public class Chart implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 分析目标
     */
    private String goal;
    /**
     * 图表名字
     */
    private String chartName;

    /**
     * 图表数据
     */
    private String chartData;

    /**
     * 图表类型
     */
    private String chartType;

    /**
     * AI生成图表数据
     */
    private String aiGenChart;

    /**
     * 生成的分析结论
     */
    private String aiGenResult;

    /**
     * userID
     */
    private Long userId;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 任务状态
     */
    private String status;

    /**
     * 执行信息
     */

    private String execMessage;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}