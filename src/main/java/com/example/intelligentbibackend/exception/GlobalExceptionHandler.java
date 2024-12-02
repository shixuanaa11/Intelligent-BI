package com.example.intelligentbibackend.exception;

import com.example.intelligentbibackend.common.BaseResponse;
import com.example.intelligentbibackend.common.ErrorCode;
import com.example.intelligentbibackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器（把接口其他的报错都返回是这样的错误，可以避免在前端泄露原始的报错信息,不暴露服务器内部状态）
 * @Author axuan
 * @Date 2022/12/6 14:43
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
//    捕获运行时业务异常
//    @ExceptionHandler(RuntimeException.class)：这是一个注解，用于指定这个方法处理RuntimeException类型的异常。当控制器中抛出RuntimeException时，Spring会自动调用这个方法来处理异常
    @ExceptionHandler(BesinessException.class)
    public BaseResponse businessExceptionHandler(BesinessException e){
        log.error("业务异常",e);
        return ResultUtils.error(e.getCode(),e.getDescription(), e.getMessage());
    }
//    捕获运行时系统内部异常
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(BesinessException e){
        log.error("系统内部异常",e);
     return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"系统内部异常", e.getMessage());
    }
}
