package com.example.intelligentbibackend.model.request.picture;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PictureUploadRequest implements Serializable {
    /**
     * 图片id
     */
    private Long id;

    @Serial
    private static final long serialVersionUID = -1840885848287862079L;
}
