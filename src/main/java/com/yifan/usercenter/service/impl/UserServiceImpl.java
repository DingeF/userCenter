package com.yifan.usercenter.service.impl;

import com.yifan.usercenter.common.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yifan.usercenter.common.BaseResponse;
import com.yifan.usercenter.common.exception.BusinessException;
import com.yifan.usercenter.constant.UserConstant;
import com.yifan.usercenter.model.domain.User;
import com.yifan.usercenter.service.UserService;
import com.yifan.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 用户操作实现类
 *
 * @author Yifan
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    public static final String SALT = "ADD_WORDS";  // 加盐


    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param plantCode     星球用户编码
     * @return 返回注册的用户id
     */
    @Override
    public BaseResponse<Long> userRegister(String userAccount, String userPassword, String checkPassword, String plantCode) {
        // 1.校验
        // 1.1 使用apache common utils库，进行非空校验以及长度校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, plantCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入参数有空值");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册账户长度不能小于4");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册密码长度不能小于8");
        }
        if (!Objects.equals(userPassword, checkPassword)) {  // 不能使用 == 比较字符串，必须使用equals方法
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册密码与校验密码不一致");
        }
        if (plantCode.length() > 5 || !StringUtils.isNumeric(plantCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册星球用户编码长度不能大于5，且必须为数字");
        }
        // 保证plantCode的唯一性
        QueryWrapper<User> queryWrapperCode = new QueryWrapper<>();
        queryWrapperCode.eq("plantCode", plantCode);
        long countCode = userMapper.selectCount(queryWrapperCode);
        if (countCode > 0) {
            return new BaseResponse<>(500, null, "注册失败");  // 星球用户编码已存在
        }


        // 1.2. 账户不包含特殊字符(正则表达式)
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        if (StringUtils.containsAny(userAccount, validPattern.toCharArray())) {
            return new BaseResponse<>(500, null, "注册失败");  // 账户包含特殊字符
        }

        // 1.3. 从数据库查询是否存在相同账户,账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册账户已存在");
        }


        // 2. 密码加密(单向算法，不用解密)
        // MD5 加密(加言)
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 3 插入数据库
        User user = new User();
        user.setPlantCode(plantCode);
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        // this指向UserServiceImpl类的实例，由于其继承了UserService接口，
        // 而UserService接口继承了IService接口，所以可以直接调用save方法
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册失败");
        }
        return ResultUtil.success(user.getId(), "注册成功");
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 用户信息(脱敏)，JSON格式
     */
    @Override
    public BaseResponse<User> userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        //1. 校验
        // 1.1 传递的用户账户与用户密码不能为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入参数有空值");
        }
        // 1.2 用户账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        if (StringUtils.containsAny(userAccount, validPattern.toCharArray())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录账户包含特殊字符");  // 账户包含特殊字符
        }
        // 1.3用户账户长度必须大于4
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录账户长度不能小于4");
        }
        // 1.4 用户密码长度必须大于8
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录密码长度不能小于8");
        }

        // 2. 判断用户账户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {  // 用户不存在
            log.info("User Login Failed:userAccount not exist");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录账户不存在");
        }

        // 3. 判断密码是否正确
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        if (!Objects.equals(encryptPassword, user.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录密码错误");
        }

        // 4. 返回用户信息(Json格式，脱敏)
        User safetyUser = getSafetyUser(user);

        // 5. 记录用户的登陆状态(将用户信息存储到session中)
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATUS, safetyUser);
        return ResultUtil.success(safetyUser, "登录成功");
    }

    /**
     * 用户注销
     *
     * @param request 请求对象
     * @return 注销状态码
     */
    @Override
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        // 删除session中的用户登录状态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATUS);
        return ResultUtil.success(1, "注销成功");
    }


    /**
     * 用户信息脱敏----防止用户信息泄露
     *
     * @param user 用户对象
     * @return 脱敏后的用户信息
     */
    @Override
    public User getSafetyUser(User user) {
        if (user == null) {
            return null;
        }

        User safetyUser = new User();

        // 只返回必要的用户信息，防止用户信息泄露
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setPlantCode(user.getPlantCode());
        safetyUser.setCreateTime(user.getCreateTime());

        return safetyUser;
    }

    @Override
    public User updateUser(User oldUser, User afterUpdateUser) {

        // 根据afterUpdateUser更新oldUser的用户信息
        oldUser.setUsername(afterUpdateUser.getUsername());
        oldUser.setUserAccount(afterUpdateUser.getUserAccount());
        oldUser.setAvatarUrl(afterUpdateUser.getAvatarUrl());
        oldUser.setGender(afterUpdateUser.getGender());
        oldUser.setUserPassword(afterUpdateUser.getUserPassword());
        oldUser.setPhone(afterUpdateUser.getPhone());
        oldUser.setEmail(afterUpdateUser.getEmail());
        oldUser.setUserStatus(afterUpdateUser.getUserStatus());
        oldUser.setUserRole(afterUpdateUser.getUserRole());
        oldUser.setPlantCode(afterUpdateUser.getPlantCode());

        // 更新用户信息
        userMapper.updateById(oldUser);

        // 返回更新后的用户信息
        return oldUser;
    }

}




