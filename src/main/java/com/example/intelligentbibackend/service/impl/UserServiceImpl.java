package com.example.intelligentbibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.intelligentbibackend.common.ErrorCode;
import com.example.intelligentbibackend.enums.UserRoleEnum;
import com.example.intelligentbibackend.exception.BesinessException;

import com.example.intelligentbibackend.model.domain.User;
import com.example.intelligentbibackend.model.vo.LoginUserVO;
import com.example.intelligentbibackend.service.UserService;
import com.example.intelligentbibackend.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.intelligentbibackend.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author HYR
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-11-26 03:05:27
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;
    /**
     * 盐值，混淆加密用的
     */
    private final String SALT = "axuan";
    /**
     * 用户登录态键
     */


    @Override
    public long userRegister(String account, String password, String checkPassword) {
//        1.注册校验(这里来写注册校验的逻辑，但是一行一行写有点麻烦，还可能漏了，我们可以通过引入一个库commons lang来辅助我们写)
//        判断这些形参是否为空
//        这里顺序有讲究的，比如第三个和第四个，如果我在已经确认账号有特殊字符的情况下就不用再去校验账号是否重复的事情了
//        因为校验已经不通过了，所以这样的顺序可以避免性能浪费
        if (StringUtils.isAnyBlank(account, password, checkPassword)) {
//            return -1;
//            抛出异常,而不是返回值，传不同的信息，前端可以分辨是什么错误
            throw new BesinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }

        if (account.length()<4){
            throw new  BesinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if (password.length()<8||checkPassword.length()<8){
            throw new  BesinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }

//       密码要与确认密码一样
        if (!password.equals(checkPassword)) {
            throw new  BesinessException(ErrorCode.PARAMS_ERROR);
        }
//      账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(account);
        if (matcher.find()){
            throw new  BesinessException(ErrorCode.PARAMS_ERROR,"账户不能包含特殊字符");
        }
//        账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new  BesinessException(ErrorCode.PARAMS_ERROR,"账户重复");
        }
//      2.给密码加密(可以找一个现成的库来实现加密)

        String encodedPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
//      3.存入数据
        User user = new User();
        user.setAccount(account);
        user.setPassword(encodedPassword);
//        这里由于是mybatis plus所以我们服务层调用mapper层的方法，mapper层调用mapper.xml的方法，mapper.xml的方法调用mapper接口的方法，所以这里我们直接调用mapper层的方法
//        这里调用save方法，save方法会自动调用mapper层的insert方法，，并且由于都给我们封装好了，所以不用去搞什么自动装配
        boolean saveResult = this.save(user);
//        这里如果没存进去就返回失败
        if (!saveResult){
            throw new  BesinessException(ErrorCode.PARAMS_ERROR,"注册失败");
        }
        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(String account, String password, HttpServletRequest request) {
//        登录校验
        if (StringUtils.isAnyBlank(account, password)) {
            throw new  BesinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (account.length()<4){
            throw new  BesinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if (password.length()<8){
            throw new  BesinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
//      账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(account);
        if (matcher.find()){
            throw new  BesinessException(ErrorCode.PARAMS_ERROR,"账户不能包含特殊字符");
        }
//      2.给密码加密(可以找一个现成的库来实现加密)

        String encodedPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        queryWrapper.eq("password", encodedPassword);
//      这段代码的作用是使用 queryWrapper 中定义的条件从数据库中查询一条记录，
//      并将查询结果映射到 Manageruser 对象中。如果查询结果有多条记录
//      selectOne 方法会抛出一个异常。如果查询结果没有记录，selectOne 方法会返回 null。
        User user = userMapper.selectOne(queryWrapper);
//        用户不存在
        if (user==null){
            log.info("login failed, account can not match password ");
            throw new  BesinessException(ErrorCode.PARAMS_ERROR,"用户名与密码不匹配");
        }



//       3.记录用户的的登陆态(将用户信息存入session中)
//        session里面存的还是user的实体类 而不是userVO
        request.getSession().setAttribute(USER_LOGIN_STATE,user);
//       4.用户脱敏（隐藏用户敏感信息,防止数据库的字段泄露给前端）

        return  this.getLoginUserVO(user);
    }



//    前端传进来一个请求,因为我们之前传进的session变量（相当于名字），是这个USER_LOGIN_STATE，所以我们现在把他移除



    /**
     * 用户注销接口
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
//        移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 用户更新編輯
     * @param
     * @return
     */
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

    @Override
    public User getloginuser(HttpServletRequest request){
//        这里其实判断了两层，为了保险
        if (request ==null){
            return null;
        }
//        在session中获取当前用户信息
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BesinessException(ErrorCode.NO_LOGIN);
        }

        return (User) userObj;
    }

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public boolean isAdmin(User user) {
        if(user == null) {
            return false;
        }
//        判断用户是否是管理员
        if (UserRoleEnum.ADMIN.getValue().equals(user.getUserRole())) {
            return false;
        }
        return true;
    }

}




