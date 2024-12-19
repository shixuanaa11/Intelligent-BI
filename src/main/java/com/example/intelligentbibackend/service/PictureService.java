package com.example.intelligentbibackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.intelligentbibackend.model.domain.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.intelligentbibackend.model.domain.User;
import com.example.intelligentbibackend.model.request.file.UploadPictureResult;
import com.example.intelligentbibackend.model.request.picture.PictureQueryRequest;
import com.example.intelligentbibackend.model.request.picture.PictureUploadRequest;
import com.example.intelligentbibackend.model.vo.PictureVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
* @author HYR
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2024-12-16 03:02:40
*/
public interface PictureService extends IService<Picture> {


    /**
     * 上传图片
     * @param multipartFile
     * @param pictureUploadRequest
     * @param LoginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User LoginUser);

    /**
     * 查询图片
     * @param pictureQueryRequest
     * @return
     */

    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取图片VO,为图片获取创建用户的信息，然后返回给前端
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 分页获取图片VO
     * @param picturePage
     * @param request
     * @return
     */

    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 图片校验
     * @param picture
     */

    void validPicture(Picture picture);
}
