package com.yifan.usercenter.service;

import com.yifan.usercenter.common.BaseResponse;
import com.yifan.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务接口
 *
 * @author Yifan
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2025-11-12 14:22:39
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param plantCode     星球用户编码
     * @return 返回注册的用户id
     */
    BaseResponse<Long> userRegister(String userAccount, String userPassword, String checkPassword, String plantCode);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 返回用户信息(脱敏)
     */
    BaseResponse<User> userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request 请求对象
     * @return 注销状态码
     */
    BaseResponse<Integer> userLogout(HttpServletRequest request);
    /**
     * 用户信息脱敏----防止用户信息泄露
     * @param user 用户对象
     * @return 脱敏后的用户信息
     */
    User getSafetyUser(User user);

    /**
     * 更新用户信息
     * @param oldUser       原始用户对象
     * @param afterUpdateUser HTTP请求传入的、待更新的用户对象
     * @return AfterUpdateUser更新后的用户对象
     */
    User updateUser(User oldUser, User afterUpdateUser);

    /**
     * 根据标签列表查询用户(所有标签都满足)
     * @param tagNameList 标签列表
     * @return 返回 safetyUser 列表
     */
    List<User> queryUsersByTagsAccordMemory(List<String> tagNameList);

    List<User> queryUsersByTagsAccordSql(List<String> tagNameList);



}
