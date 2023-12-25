package org.example.service.impl;

import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyService;
import org.example.mapper.UserMapper;
import org.example.service.LoginService;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyService
public class LoginServiceImpl implements LoginService {
    @MyAutoWired
    UserMapper userMapper;
    @Override
    public boolean login(String username, String password) {
        return userMapper.login(username, password) != null;
    }

    @Override
    public Integer insertUser(String username, String password) {
        return userMapper.insertUser(username,password);
    }
}
