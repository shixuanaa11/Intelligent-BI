package com.example.intelligentbibackend.controller;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.intelligentbibackend.common.BaseResponse;
import com.example.intelligentbibackend.common.DeleteRequest;
import com.example.intelligentbibackend.common.ErrorCode;
import com.example.intelligentbibackend.common.ResultUtils;
import com.example.intelligentbibackend.exception.BesinessException;
import com.example.intelligentbibackend.manager.CosManager;
import com.example.intelligentbibackend.model.domain.Picture;
import com.example.intelligentbibackend.model.domain.User;
import com.example.intelligentbibackend.model.request.picture.*;
import com.example.intelligentbibackend.model.vo.PictureTagCategory;
import com.example.intelligentbibackend.model.vo.PictureVO;
import com.example.intelligentbibackend.service.PictureService;
import com.example.intelligentbibackend.service.UserService;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/picture")
@CrossOrigin(origins = {"http://localhost:5176"},allowCredentials = "true")
@Slf4j
public class PictureController {

    @Resource
    private CosManager cosManager;

    @Resource
    private UserService userService;
    @Resource
    private PictureService pictureService;

    /**
     * 文件上传
     *
     * @param multipartFile
     * 有个管理员权限，后面补
     * @return
     */
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/upload")
    public BaseResponse<PictureVO> UploadFile(@RequestPart("file") MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, HttpServletRequest request) {
        User loginuser = userService.getloginuser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginuser);
//        这里有个bug，如果Spring为了防止文件攻击所以限制了上传文件的大小，所以我们要主动开启
        return ResultUtils.success(pictureVO);

    }

    /**
     * 测试文件下载
     *
     * @param filepath 文件路径
     * @param response 响应对象
     */
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download/")
    public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BesinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }

//  下面是增删改查
    /**
     * 删除图片,这里删除是本人和管理员都能删，所以不用权限注解，在代码里面校验权限
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest,HttpServletRequest request) {

        if (deleteRequest ==null || deleteRequest.getId() <= 0){
            throw new BesinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        Long id = deleteRequest.getId();
        User loginuser = userService.getloginuser(request);
//        先数据查到这个图表id的图表
        Picture oldPicture = pictureService.getById(id);
        if(oldPicture==null){
           throw new BesinessException(ErrorCode.PARAMS_ERROR,"数据不存在");
        }
//        只有本人或者管理员才可以删除
        if (!oldPicture.getUserId().equals(loginuser.getId()) && !loginuser.getUserRole().equals("admin")){
            throw new BesinessException(ErrorCode.NO_PERMISSION,"没有权限");
        }
        boolean result = pictureService.removeById(id);
        if (!result) {
            throw new BesinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);

    }


    /**
     * 更新图片（仅管理员可用）
     */
    @PostMapping("/update")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest) {
        if (pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0) {
            throw new BesinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 数据校验
        pictureService.validPicture(picture);
        // 判断是否存在
        long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        if (oldPicture == null){
            throw new BesinessException(ErrorCode.PARAMS_ERROR);
        }
        // 操作数据库
        boolean result = pictureService.updateById(picture);
        if (!result){
            throw new BesinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取图片（仅管理员可用）
     */
    @GetMapping("/get")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Picture> getPictureById(long id, HttpServletRequest request) {
        if (id <= 0){
            throw new BesinessException(ErrorCode.NULL_ERROR);
        }

        // 查询数据库
        Picture picture = pictureService.getById(id);
        if (picture==null) {
            throw new BesinessException(ErrorCode.PARAMS_ERROR, "数据不存在");
        }
        // 获取封装类
        return ResultUtils.success(picture);
    }

    /**
     * 根据 id 获取图片（封装类）
     */
    @GetMapping("/get/vo")
    public BaseResponse<PictureVO> getPictureVOById( Long id, HttpServletRequest request) {
        if (id <= 0){
            throw new BesinessException(ErrorCode.PARAMS_ERROR);
        }
        // 查询数据库
        Picture picture = pictureService.getById(id);
        if (picture == null) {
            throw new BesinessException(ErrorCode.PARAMS_ERROR, "数据不存在");
        }

        // 获取封装类（request沒用啊）
        return ResultUtils.success(pictureService.getPictureVO(picture, request));
    }

    /**
     * 分页获取图片列表（仅管理员可用）
     */
    @PostMapping("/list/page")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        return ResultUtils.success(picturePage);
    }

    /**
     * 分页获取图片列表（封装类）
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                             HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        if (size > 20){
            throw new BesinessException(ErrorCode.PARAMS_ERROR);
        }
        // 查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                pictureService.getQueryWrapper(pictureQueryRequest));
        // 获取封装类
        return ResultUtils.success(pictureService.getPictureVOPage(picturePage, request));
    }

    /**
     * 编辑图片（给用户使用）
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest, HttpServletRequest request) {
        if (pictureEditRequest == null || pictureEditRequest.getId() <= 0) {
            throw new BesinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        // 设置编辑时间
        picture.setEditTime(new Date());
        // 数据校验
        pictureService.validPicture(picture);
        User loginUser = userService.getloginuser(request);
        // 判断是否存在
        long id = pictureEditRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        if (oldPicture == null){
            throw new BesinessException(ErrorCode.PARAMS_ERROR);
        }

        // 仅本人或管理员可编辑
        if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BesinessException(ErrorCode.NO_PERMISSION);
        }
        // 操作数据库
        boolean result = pictureService.updateById(picture);
        if (!result){
            throw new BesinessException(ErrorCode.PARAMS_ERROR,"更新失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 主页列表接口
     * 不要在前端写死，在后端定义 前端动态获取 会省很多事 后面字段增多考虑建表
     * @return
     */
    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
        List<String> categoryList = Arrays.asList("模板", "电商", "表情包", "素材", "海报");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return ResultUtils.success(pictureTagCategory);
    }




}
