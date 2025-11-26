package com.yifan.usercenter.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * 基础响应类  ----- 返回给前端的对象
 * @param <T> 响应数据类型是通用类型，因此用泛型T
 */
@Data
public class BaseResponse<T> implements Serializable {

    /**
     * 状态码
     * 200 成功
     * 400 客户端错误
     * 500 服务器错误
     * 600 自定义错误
     */
    private int code;

     /**
      * 响应数据
      */
    private T data;

     /**
      * 响应消息
      */
    private String msg;

    /**
     * 响应描述
     */
    private String description;

    public BaseResponse(int code, T data, String msg, String description  ) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.description = description;
    }

    public BaseResponse(int code, T data, String msg) {
        this(code, data, msg, "");
    }

    public BaseResponse(int code, T data) {
        this(code, data, "", "");
    }

    public BaseResponse(ErrorCode errorCode) {  // 报错时data返回为null
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }


}
