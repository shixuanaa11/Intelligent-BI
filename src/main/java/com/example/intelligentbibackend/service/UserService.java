package com.example.intelligentbibackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.intelligentbibackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.intelligentbibackend.model.request.user.UserQueryRequest;
import com.example.intelligentbibackend.model.vo.LoginUserVO;
import com.example.intelligentbibackend.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author HYR
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-11-26 03:05:27
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册接口
     *
     * @param account       账号
     * @param password      密码
     * @param checkPassword 确认密码
     * @return
     */

    long userRegister(String account, String password, String checkPassword);

    String getEncryptPassword(String password);

    /**
     * 用户登录接口
     *
     * @param account  账号
     * @param password 密码
     * @return
     */
    LoginUserVO userLogin(String account, String password, HttpServletRequest request);


    /**
     * 用户注销接口
     * @param request
     * @return
     */

    int userLogout(HttpServletRequest request);


    /**
     * 获取session的用户信息
     * @param request
     * @return
     */
    User getloginuser(HttpServletRequest request);

    /**
     * 将用户变成queryWrapper类型
     * @param userQueryRequest
     * @return
     */

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 脱敏用户信息
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);


    /**
     * 判断用户是否为管理员
     */
    boolean isAdmin(User user);

    /**
     * 管理员获取用户信息
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 管理员获取用户列表
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

}
