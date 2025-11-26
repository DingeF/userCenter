package com.yifan.usercenter.model.domain.request;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 用户登录请求参数
 * @author yifan
 * @version 1.0
 * &#064;date  2025/11/13 16:07
 * &#064;description  用户登录请求参数
 */
@Data
public class UserLogInRequest implements Serializable {

    /**
     * 序列化版本号，用于在反序列化时验证类的版本是否兼容
     */
    private static final long serialVersionUID = 1L;

    /**
     * 用户账户
     */
    private String userAccount;
    /**
     * 用户密码
     */
    private String userPassword;
}
