package com.yifan.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author Yifan
 */
@Data
public class UserRegisterRequest implements Serializable {

    /**
     * 序列化版本号，用于在反序列化时验证类的版本是否兼容
     */
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户账户
     */
    private String userAccount;
    /**
     * 用户密码
     */
    private String userPassword;
    /**
     * 校验密码
     */
    private String checkPassword;

    /**
     * 星球用户编码
     */
    private String plantCode;
}
