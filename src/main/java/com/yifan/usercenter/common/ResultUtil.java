package com.yifan.usercenter.common;

public class ResultUtil {

    /**
     * 成功响应
     * @param data 响应数据
     * @param msg 响应消息
     * @return 成功响应对象
     * @param <T> 响应数据类型
     */
    public static <T> BaseResponse<T> success(T data,String msg) {
        return new BaseResponse<>(ErrorCode.SUCCESS.getCode(), data, msg);
    }

    /**
     * 错误响应
     * @param errorCode 错误码枚举
     * @return 错误响应对象
     * @param <T> 响应数据类型
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }

    /**
     * 错误响应
     * @param errorCode 错误码枚举
     * @param msg 错误消息
     * @param description 错误描述
     * @return 错误响应对象
     * @param <T> 响应数据类型
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String msg, String description) {
        return new BaseResponse<>(errorCode.getCode(), null, msg, description);
    }

     /**
     * 错误响应
     * @param errorCode 错误码枚举
     * @param description 错误描述
     * @return 错误响应对象
     * @param <T> 响应数据类型
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String description) {
        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMessage(), description);
    }

     /**
     * 错误响应
     * @param code 错误码
     * @param msg 错误消息
     * @param description 错误描述
     * @return 错误响应对象
     * @param <T> 响应数据类型
     */
    public static <T> BaseResponse<T> error(int code, String msg, String description) {
        return new BaseResponse<>(code, null, msg, description);
    }
}
