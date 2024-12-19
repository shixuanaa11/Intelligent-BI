package com.example.intelligentbibackend.service;

import com.example.intelligentbibackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.intelligentbibackend.model.vo.LoginUserVO;
import jakarta.servlet.http.HttpServletRequest;

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

    //    @Override
//
//    public int updateUser(User user) {
//
//        long id = user.getId();
//        if (id <=0){
//            throw new BesinessException(ErrorCode.PARAMS_ERROR);
//        }
////       判断用户是否为管理员或者要修改的用户是否为当前用户，如果都不是的话就无权限
////        if (!isAdmin(loginuser) && id != loginuser.getId()) {
////            throw new BesinessException(ErrorCode.NO_PERMISSION);
////        }
//        //            通过id找数据库里面的对应数据
//        Manageruser searchuser = userMapper.selectById(id);
//        if (searchuser == null) {
//            throw new BesinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
//        }
//        return userMapper.updateById(manageruser);
//
//    }
//    @Override
//    public List<Manageruser> searchUser(Manageruser manageruser) {
//        QueryWrapper<Manageruser> queryWrapper = new QueryWrapper<>();
//        queryWrapper.like(StringUtils.isNotBlank(manageruser.getUsername()),"username", manageruser.getUsername())
//                .like(StringUtils.isNotBlank(manageruser.getAccount()),"account", manageruser.getAccount())
//                .eq(manageruser.getGender()>0,"gender",manageruser.getGender())
//                .eq(manageruser.getStatus()>0,"status",manageruser.getStatus())
//                .eq(manageruser.getUserRole()>0,"userRole",manageruser.getUserRole())
//                .like(StringUtils.isNotBlank(manageruser.getPhone()),"phone",manageruser.getPhone());
//
////       最后还有一个时间
////        queryWrapper.between("createTime", manageruser.getCreateTime());
//        List<Manageruser> userlist = userMapper.selectList(queryWrapper);
//        return userlist;
//
//    }

    /**
     * 获取session的用户信息
     * @param request
     * @return
     */
    User getloginuser(HttpServletRequest request);

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

}
