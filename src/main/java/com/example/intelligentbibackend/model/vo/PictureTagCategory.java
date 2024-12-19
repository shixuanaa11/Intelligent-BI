package com.example.intelligentbibackend.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class PictureTagCategory implements Serializable {
    @Serial
    private static final long serialVersionUID = 3525897215831893078L;

    /**
     * 主页标签列表
     */
    private List<String> TagList;

    /**
     * 主页分类列表
     */
    private List<String> CategoryList;
}
