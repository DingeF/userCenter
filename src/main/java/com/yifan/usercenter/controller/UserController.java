package com.yifan.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yifan.usercenter.common.BaseResponse;
import com.yifan.usercenter.common.ResultUtil;
import com.yifan.usercenter.common.ErrorCode;
import com.yifan.usercenter.common.exception.BusinessException;
import com.yifan.usercenter.constant.UserConstant;
import com.yifan.usercenter.mapper.UserMapper;
import com.yifan.usercenter.model.domain.User;
import com.yifan.usercenter.model.domain.request.UserLogInRequest;
import com.yifan.usercenter.model.domain.request.UserQueryRequest;
import com.yifan.usercenter.model.domain.request.UserRegisterRequest;
import com.yifan.usercenter.service.UserService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.yifan.usercenter.constant.UserConstant.ALLOWED_SORT_FIELDS;


/**
 * 用户接口
 *
 * @author yifan
 * @version 1.0
 * &#064;date  2025/11/13 15:49
 * &#064;description  用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;  // 自动注入UserService Bean对象
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求参数
     * @return 新用户ID
     */
    @PostMapping("/register")
    public BaseResponse<Long> registerUser(@RequestBody UserRegisterRequest userRegisterRequest) {  // @RequestBody 注解用于将请求体中的JSON数据绑定到UserRegisterRequest对象
        if (userRegisterRequest == null) {
           throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户注册请求为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String plantCode = userRegisterRequest.getPlantCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, plantCode)) {
           throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册参数为空");
        }

        return userService.userRegister(userAccount, userPassword, checkPassword, plantCode);
    }

    /**
     * 用户登录
     *
     * @param userLogInRequest 用户登录请求参数
     * @return 用户信息(脱敏)，JSON格式
     */
    @PostMapping("/login")
    public BaseResponse<User> loginUser(@RequestBody UserLogInRequest userLogInRequest, HttpServletRequest request) {
        if (userLogInRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户登录请求为空");
        }
        String userAccount = userLogInRequest.getUserAccount();
        String userPassword = userLogInRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录参数为空");
        }
        return userService.userLogin(userAccount, userPassword, request);
    }

    /**
     * 用户注销
     * @param request 请求对象
     * @return 返回注销状态码
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> logoutUser(HttpServletRequest request) {
        if(request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户注销请求对象为空");
        }
        return userService.userLogout(request);
    }

    /**
     * 用户查询
     * 根据用户名查询用户列表
     * 允许用户名为空
     *
     * @param queryRequest 查询请求对象
     * @return 用户列表
     */
    @PostMapping("/query")
    public BaseResponse<List<User>> queryUserList(@RequestBody UserQueryRequest queryRequest, HttpServletRequest request) {
        // 仅管理员可查询
        if (!isAdmin(request)) {
           throw new BusinessException(ErrorCode.NO_AUTH, "用户查询权限不足");
        }
        QueryWrapper<User> qw = new QueryWrapper<>();
        if (StringUtils.isNotBlank(queryRequest.getUserAccount())) { qw.like("userAccount", queryRequest.getUserAccount()); }
        if (StringUtils.isNotBlank(queryRequest.getUsername())) { qw.like("username", queryRequest.getUsername()); }
        if (StringUtils.isNotBlank(queryRequest.getPlantCode())) { qw.like("plantCode", queryRequest.getPlantCode()); }
        if (queryRequest.getGender() != null) { qw.eq("gender", queryRequest.getGender()); }
        if (StringUtils.isNotBlank(queryRequest.getPhone())) { qw.like("phone", queryRequest.getPhone()); }
        if (StringUtils.isNotBlank(queryRequest.getEmail())) { qw.like("email", queryRequest.getEmail()); }
        if (queryRequest.getUserStatus() != null) { qw.eq("userStatus", queryRequest.getUserStatus()); }
        if (queryRequest.getUserRole() != null) { qw.eq("userRole", queryRequest.getUserRole()); }
        if (queryRequest.getDateTime() != null) {    qw.ge("createTime", queryRequest.getDateTime())
                                                        .lt("createTime", queryRequest.getDateTime().plusDays(1)); }

       // 排序字段校验，指定字段进行排序
        if (StringUtils.isNotBlank(queryRequest.getSortField()) && ALLOWED_SORT_FIELDS.contains(queryRequest.getSortField())) {
            String field = queryRequest.getSortField();
            String order = queryRequest.getSortOrder();
            if ("asc".equalsIgnoreCase(order)) { qw.orderByAsc(field); }
            else if ("desc".equalsIgnoreCase(order)) { qw.orderByDesc(field); }
        }
        List<User> list = userService.list(qw);
        List<User> safe = list.stream().map(userService::getSafetyUser).collect(Collectors.toList());
        return ResultUtil.success(safe, "查询成功");
    }




    /**
     * 用户删除
     * Mybatis-plus框架会自动改造为逻辑删除(自动转变为更新)
     * 根据用户ID删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        // 仅管理员可删除
        if (!isAdmin(request)) {
           throw new BusinessException(ErrorCode.NO_AUTH, "用户删除权限不足");
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID参数错误");
        }
        return ResultUtil.success(userService.removeById(id), "删除成功");
    }

    @PostMapping("/update")
    public BaseResponse<User> updateUser(@RequestBody User user, HttpServletRequest request){
        // 仅管理员可更新
        if (!isAdmin(request)) {
           throw new BusinessException(ErrorCode.NO_AUTH, "用户更新权限不足");
        }
        if (user == null || user.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID参数错误");
        }
        // 保留传入的user的Id不变，其余值都根据user的属性值进行更新
        User oldUser = userService.getById(user.getId());
        return ResultUtil.success(userService.updateUser(oldUser, user), "更新成功");
    }


    /**
     * 获取当前用户登录态
     *
     * @param request HTTP请求对象，用于获取会话中的用户登录状态
     * @return 当前用户信息
     */
    @GetMapping("/currentUser")
    public User getCurrentUser(HttpServletRequest request) {
        Object userLoginStatus = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        User currentUser = (User) userLoginStatus;
        if (currentUser == null) {
            return null;
        }
        Long userId = currentUser.getId();
        // TODO:当前未被删除即可返回用户信息：缺少用户若被封号时的状态判断，后续可加入
        return userService.getSafetyUser(userService.getById(userId));
    }

    /**
     * 判断用户是否为管理员
     *
     * @param request HTTP请求对象，用于获取会话中的用户登录状态
     * @return 如果用户是管理员则返回true，否则返回false
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询(通过在UserService中声明的用户登陆状态键来查询用户是否为管理员)
        Object userLoginStatus = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        User user = (User) userLoginStatus;

        return user != null && user.getUserRole() == UserConstant.USER_ADMIN_ROLE;
    }
}
