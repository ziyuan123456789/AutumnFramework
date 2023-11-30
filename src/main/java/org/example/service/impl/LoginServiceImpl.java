package org.example.service.impl;

import org.example.Bean.User;
import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyService;
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
}
