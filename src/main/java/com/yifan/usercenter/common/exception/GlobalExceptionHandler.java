package com.yifan.usercenter.common.exception;

import com.yifan.usercenter.common.BaseResponse;
import com.yifan.usercenter.common.ErrorCode;
import com.yifan.usercenter.common.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理器
 * 用于处理应用程序中抛出的异常，返回统一的错误响应
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends RuntimeException {
    @ExceptionHandler(BusinessException.class)
    public BaseResponse handleBusinessException(BusinessException be){
        log.error("BusinessException: " + be.getMessage(), be);
        return ResultUtil.error(be.getCode(), be.getMessage(), be.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse handleRuntimeException(RuntimeException re){
        log.error("RuntimeException: " + re.getMessage(), re);
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR, re.getMessage());
    }
}
