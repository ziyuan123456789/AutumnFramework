package org.example.mapper;

/**
 * @author ziyuan
 * @since 2024.09
 */
public interface UpdateMapper {
    int insertUser(String username, String role, String password, String Salt);

    int updateUserById(String username, String role, String password, Integer userID);

    int deleteUserById(Integer userId);
}
