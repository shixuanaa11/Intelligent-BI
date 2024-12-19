package com.example.intelligentbibackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.intelligentbibackend.common.ErrorCode;
import com.example.intelligentbibackend.exception.BesinessException;
import com.example.intelligentbibackend.manager.FileManager;
import com.example.intelligentbibackend.mapper.PictureMapper;
import com.example.intelligentbibackend.model.domain.Picture;
import com.example.intelligentbibackend.model.domain.User;
import com.example.intelligentbibackend.model.request.file.UploadPictureResult;
import com.example.intelligentbibackend.model.request.picture.PictureQueryRequest;
import com.example.intelligentbibackend.model.request.picture.PictureUploadRequest;
import com.example.intelligentbibackend.model.vo.LoginUserVO;
import com.example.intelligentbibackend.model.vo.PictureVO;
import com.example.intelligentbibackend.service.PictureService;
import com.example.intelligentbibackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author HYR
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2024-12-16 03:02:40
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

    @Resource
    private FileManager fileManager;

    @Resource
    private UserService userService;

    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User LoginUser) {
//        校验参数
        if (LoginUser==null){
            throw new BesinessException(ErrorCode.NO_PERMISSION,"未登录");
        }
//        判断是新增还是删除 (通过有没有传（pictureUploadRequest）图片id来判断是删除 新增)
        Long pictureId = null;
        if (pictureUploadRequest!=null){
//            如果请求的对象（图片id）不为空，就给他赋值
            pictureId = pictureUploadRequest.getId();
        }
//        如果是更新判断图片存不存在
//        todo:有点懵，后面回来理清
        if (pictureId!=null){
            Picture picture = this.getById(pictureId);
            if (picture==null){
                throw new BesinessException(ErrorCode.PARAMS_ERROR,"图片不存在");
            }
        }
//        上传图片，得到图片信息
//          设置文件路径前缀
        String uploadPathPrefix=String.format("public/%s",LoginUser.getId());
        UploadPictureResult uploadPictureResult = fileManager.uploadPicture(multipartFile, uploadPathPrefix);
//        封装图片信息,如果数据量再大的话,可以用更加方便的方法BeanUtil
        Picture picture = new Picture();
        picture.setId(0L);
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setName(uploadPictureResult.getPicName());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(LoginUser.getId());
//        操作数据库
//        如果pictureId不为空，就更新，否则就新增
        if (picture!=null){
//        如果是更新就需要补充id和更新时间
            picture.setId(pictureId);
            picture.setUpdateTime(new Date());
        }
//        保存或者更新，mybatis plus的方法，会根据你传入的数据有没有id来判断是新增还是更新
        boolean result = this.saveOrUpdate(picture);
        if (!result){
            throw new BesinessException(ErrorCode.SYSTEM_ERROR,"图片保存失败");
        }
//        返回图片信息，将实体类转VO
        return PictureVO.objToVo(picture);
    }

    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("name", searchText)
                    .or()
                    .like("introduction", searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // 对象转封装类
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 关联查询用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            LoginUserVO userVO = userService.getLoginUserVO(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }

    /**
     * 分页获取图片封装
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getLoginUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    @Override
    public void validPicture(Picture picture) {
        if(picture == null){
         throw new BesinessException(ErrorCode.PARAMS_ERROR);
        }
        // 从对象中取值
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 修改数据时，id 不能为空，有参数则校验
        if (ObjUtil.isNull(id)){
            throw  new BesinessException(ErrorCode.PARAMS_ERROR,"id 不能为空");
        }
        if (StrUtil.isNotBlank(url)) {
            if (url.length() > 1024){
                throw new BesinessException(ErrorCode.PARAMS_ERROR,"url 过长");
            }
        }
        if (StrUtil.isNotBlank(introduction)) {
            if (introduction.length() > 800){
                throw new BesinessException(ErrorCode.PARAMS_ERROR,"简介过长");
            }
        }
    }



}




