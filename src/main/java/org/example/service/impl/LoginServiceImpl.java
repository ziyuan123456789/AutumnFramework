package org.example.service.impl;

import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyService;
import org.example.mapper.UserMapper;
import org.example.service.LoginService;

/**
 * @author ziyuan
 * @since 2024.04
 */
@MyService
public class LoginServiceImpl implements LoginService {
    @MyAutoWired
    UserMapper userMapper;

    @Override
    public boolean checkLogin(String userId, String password) {
        return userMapper.checkUser(userId, password) != null;
    }
}
