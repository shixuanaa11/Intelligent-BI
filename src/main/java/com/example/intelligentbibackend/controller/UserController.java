package com.example.intelligentbibackend.controller;

import com.example.intelligentbibackend.common.BaseResponse;
import com.example.intelligentbibackend.common.ErrorCode;
import com.example.intelligentbibackend.common.ResultUtils;
import com.example.intelligentbibackend.exception.BesinessException;
import com.example.intelligentbibackend.model.domain.User;
import com.example.intelligentbibackend.model.request.user.UserLoginRequest;
import com.example.intelligentbibackend.model.request.user.UserRegisterRequest;
import com.example.intelligentbibackend.model.vo.LoginUserVO;
import com.example.intelligentbibackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5175"},allowCredentials = "true")
public class UserController {



    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest) {
//        判断传的对象是否为空
        if (userRegisterRequest == null) {
//            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
//            不返回值了，抛出异常，报错
            throw new BesinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getAccount();
        String userPassword = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BesinessException(ErrorCode.PARAMS_ERROR);
        }
//        最后返回一个id值
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO>  login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getAccount();
        String userPassword = userLoginRequest.getPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
//        返回一个登录对象的脱敏数据
        LoginUserVO user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

//    注销
@PostMapping("/logout")
public BaseResponse<Integer> logout( HttpServletRequest request) {
    if (request == null) {
        return ResultUtils.error(ErrorCode.PARAMS_ERROR);
    }
    int result = userService.userLogout(request);
    return ResultUtils.success(result);
}

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUserInfo(HttpServletRequest request) {
        User user = userService.getloginuser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    //       增删改查
//        查
//    @GetMapping("/search")
//    public BaseResponse<List<Manageruser>> searchManagerUserList(String username,HttpServletRequest request) {
//
////       判断是否是管理员
//        if (!isAdmin(request)) {
//        throw new BesinessException(ErrorCode.NO_PERMISSION,"用户不是管理员");
//        }
//
//        QueryWrapper<Manageruser> queryWrapper = new QueryWrapper<>();
////        如果为空的话就不用条件去查询了,因为你要查是null的数据,给了反而查不到
//        if (StringUtils.isNotBlank(username)) {
// //        下面这行代码的意思是，首先给两个形参“username" username,column形参的"username",表示要到数据库中查询符合条件username列
////        然后会返回符合条件的那一行数据库中的数据
//            queryWrapper.like("username", username);
//        }
//
//
////        这行再去数据库里查一遍，然后返回list集合
//        List<Manageruser> Userlist = manageruserService.list(queryWrapper);
//        List<Manageruser> List = Userlist.stream().map(manageruser -> manageruserService.getsafetyManageruser(manageruser)).collect(Collectors.toList());
//        return ResultUtils.success(List);
//    }

    //        删
//    @PostMapping("/delete")
//// 打@RequestBody是因为要获取前端传过来的id
//    public BaseResponse<Boolean> deleteManagerUserList(@RequestParam long id, HttpServletRequest request) {
////       判断是否是管理员
//        if (!isAdmin(request)) {
//            throw new BesinessException(ErrorCode.NO_PERMISSION,"用户不是管理员");
//        }
//
//        if (id < 0) {
////            return null;
//            throw new BesinessException(ErrorCode.PARAMS_ERROR);
//        }
////        根据id删除(而且是逻辑删除，就是你在前台删除这条数据，数据就不会再显示了，功能跟一般删除一样，但是这条数据一直保存在数据库里面)
//        boolean result = manageruserService.removeById(id);
//        return ResultUtils.success(result);
//    }
//    /**
//     * 更新用户信息
//     */
//    @PostMapping("/update")
//    public BaseResponse<Integer> updateManagerUserList(@RequestBody Manageruser manageruser,HttpServletRequest request) {
//        if (manageruser == null) {
//            throw new BesinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
//        }
//        if (!isAdmin(request)) {
//            throw new BesinessException(ErrorCode.NO_PERMISSION,"用户不是管理员");
//        }
////    我们把获取当前用户（session获取）封装在service层里，以后就不用在这里写了，直接调方法拿到当前登录用户
////        Manageruser loginuser = manageruserService.getloginuser(request);
//
////        服务层修改的方法
//        int result = manageruserService.updateUser(manageruser);
//        return ResultUtils.success(result);
//    }

//   @PostMapping("/searchUser")
//    public BaseResponse<List<Manageruser>> searchUser(@RequestBody Manageruser manageruser,HttpServletRequest request) {
//       if (!isAdmin(request)) {
//           throw new BesinessException(ErrorCode.NO_PERMISSION,"用户不是管理员");
//       }
//
//       if (manageruser == null) {
//           throw new BesinessException(ErrorCode.PARAMS_ERROR,"请求参数不能为空");
//       }
//       List<Manageruser> managerlist = manageruserService.searchUser(manageruser);
//       return ResultUtils.success(managerlist);
//   }

//    将是否为管理员封装成一个方法，避免代码重复

    /**
     * 判断是否为管理员
     * @param request
     * @return
     */
//    private boolean isAdmin(HttpServletRequest request) {
//        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User user=(User) attribute;
////        如果是就是true如果不是就是false
//        return user != null && user.getUserRole() == ADMIN_ROLE;
//    }
}
