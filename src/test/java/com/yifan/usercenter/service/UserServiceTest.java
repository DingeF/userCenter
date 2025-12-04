package com.yifan.usercenter.service;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.yifan.usercenter.common.BaseResponse;
import com.yifan.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 *
 * @author yifan
 * */
@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;

    @Test
    void TestUserRegister(){
        String userAccount = "yifan";
        String userPassword = "";
        String checkPassword = "123456";
        String plantCode = "12345";
        BaseResponse<Long> result = userService.userRegister(userAccount, userPassword, checkPassword, plantCode);
        Assertions.assertEquals(-1,result.getCode());
        // 校验账户为空
        userAccount = "";
        result = userService.userRegister(userAccount, userPassword, checkPassword, plantCode);
        Assertions.assertEquals(-1,result);
        // 校验账户长度小于4
        userAccount = "yi";
        result = userService.userRegister(userAccount, userPassword, checkPassword, plantCode);
        Assertions.assertEquals(-1,result);
        // 校验密码为空
        userAccount = "yifan";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword, plantCode);
        Assertions.assertEquals(-1, result );
        // 校验 校验密码为空
        userAccount = "yifan";
        userPassword = "123456";
        checkPassword = "";
        result = userService.userRegister(userAccount, userPassword, checkPassword, plantCode);
        Assertions.assertEquals(-1,result);
        // 校验 校验密码与密码不一致
        userAccount = "yifan";
        userPassword = "123456";
        checkPassword = "1234567";
        result = userService.userRegister(userAccount, userPassword, checkPassword, plantCode);
        Assertions.assertEquals(-1,result);
        // 校验 校验密码与密码一致
        userAccount = "yifan";
        userPassword = "123456";
        checkPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword, plantCode);
        Assertions.assertEquals(-1, result );
        // 校验 账户不能重复注册
        userAccount = "yifan";
        userPassword = "123456";
        checkPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword, plantCode);
        Assertions.assertEquals(-1,result);
        // 校验 账户不能包含特殊字符
        userAccount = "yifan@";
        userPassword = "123456";
        checkPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword, plantCode);
        Assertions.assertEquals(-1,result);
        // 插入用户成功
        userAccount = "Yifan1";
        userPassword = "12345678910";
        checkPassword = "12345678910";
        result = userService.userRegister(userAccount, userPassword, checkPassword, plantCode);
        Assertions.assertTrue(result.getData() != null && result.getData() > 0);
    }

    @Test
     void queryUsersByTagsAccordSql(){
        List<String> tagNameList = Arrays.asList("java", "python");
        List<User> userList = userService.queryUsersByTagsAccordSql(tagNameList);
        Assertions.assertTrue(userList != null && userList.size() > 0);
    }

    @Test
     void queryUsersByTagsAccordMemory(){
        List<String> tagNameList = Arrays.asList("java", "python");
        List<User> userList = userService.queryUsersByTagsAccordMemory(tagNameList);
        Assertions.assertTrue(userList != null && userList.size() > 0);
    }
}