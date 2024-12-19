package com.example.intelligentbibackend.manager;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.example.intelligentbibackend.common.ErrorCode;
import com.example.intelligentbibackend.common.ResultUtils;
import com.example.intelligentbibackend.config.CosClientConfig;
import com.example.intelligentbibackend.exception.BesinessException;
import com.example.intelligentbibackend.model.request.file.UploadPictureResult;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.OriginalInfo;
import com.qcloud.cos.utils.IOUtils;
import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 文件通用方法类
 */

    @Service
    @Slf4j
public class FileManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    /**
     * 用户上传图片通用方法
     * @param multipartFile
     * @param uploadPathPrefix
     * @return
     */

    public UploadPictureResult uploadPicture(MultipartFile multipartFile,String uploadPathPrefix) {
//       校验图片
        validPicture(multipartFile);
//       图片上传地址
//         随机生成16为字符串放在url地址前面避免重复
        String uuid = RandomUtil.randomString(16);
//        获取上传文件的原始文件名 example.txt的文件，那么multipartFile.getOriginalFilename()将返回example.txt
        String originalFilename = multipartFile.getOriginalFilename();
//        自己拼接文件名字，避免重复和增加安全性
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,FileUtil.getSuffix(originalFilename));
//        定义文件路径，把不同用户的图片放到不同的文件夹下,文件夹的前缀由用户的id定义
        String uploadPath = String.format("/%s/%s", uploadPathPrefix,uploadFileName);
//       解析结果并返回
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(uploadPath, null);
            multipartFile.transferTo(file);
//            对象存储到腾讯云
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
//            获取图片信息
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
//            图片的宽高和宽高比 图片格式
            int width = imageInfo.getWidth();
            int height = imageInfo.getHeight();
            double scale = NumberUtil.round(width *1.0 / height, 2).doubleValue();
            String format = imageInfo.getFormat();

//            封装返回结果
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath );
            uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setPicWidth(width);
            uploadPictureResult.setPicHeight(height);
            uploadPictureResult.setPicScale(scale);
            uploadPictureResult.setPicFormat(format);

            // 图片结果
            return uploadPictureResult;
        } catch (Exception e) {
            log.error("file upload error, filepath = " + uploadPath, e);
            throw new BesinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
//            临时文件清理
            deleteTempFile(file);
        }
    }


    /**
     * 图片的校验逻辑
     * @param multipartFile
     */
    private void validPicture(MultipartFile multipartFile) {
//        检验文件大小
        long size = multipartFile.getSize();
        final long ONE_M=1024 * 1024 ;
        if (size > ONE_M) {
            throw new BesinessException(ErrorCode.PARAMS_ERROR,"文件大小不能超过1M");
        }
//        校验文件后缀
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
//        允许上传的文件列表
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("png", "jpg", "jpeg", "bmp", "gif","webp");

        if (!ALLOW_FORMAT_LIST.contains(suffix)) {
            throw new BesinessException(ErrorCode.PARAMS_ERROR,"文件类型错误");
        }
    }

    /**
     * 图片的异常逻辑
     * @param file
     */
    public  void deleteTempFile(File file) {
        if (file != null) {
            return;
        }
        // 删除临时文件
        boolean delete = file.delete();
        if (!delete) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }



}
