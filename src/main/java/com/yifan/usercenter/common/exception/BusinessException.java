package com.yifan.usercenter.common.exception;

import com.yifan.usercenter.common.ErrorCode;
import lombok.Getter;

/**
 * 自定义的全局异常类
 * 给原有的异常类扩充了两个字段code与description
 * 由于无需set异常，因此加上了final关键字
 *
 * @author Yifan
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    private final String description;

    public BusinessException(int code, String message, String description){
        super(message);  // 将message传递到父级的构造函数中
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());  // 将message传递到父级的构造函数中
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description){
        super(errorCode.getMessage());  // 将message传递到父级的构造函数中
        this.code = errorCode.getCode();
        this.description = description;
    }

}
