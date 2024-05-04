package org.example.mapper;

import org.example.Bean.User;


import java.util.List;

/**
 * @author ziyuan
 * @since 2024.04
 */

public interface UserMapper {

    List<User> getOneUser(Integer userId);
    List<User> getAllUser(Integer userId);
    User checkUser(String userId, String password);
}