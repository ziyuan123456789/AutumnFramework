package org.example.mapper;

import org.example.Bean.User;
import org.example.FrameworkUtils.Annotation.MyMapper;
import org.example.FrameworkUtils.Annotation.MyParam;
import org.example.FrameworkUtils.Annotation.MySelect;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
@MyMapper
public interface UserMapper {
    @MySelect("select username,password from user where username=#{username} and password=#{password}")
    User login(@MyParam("username") String username,@MyParam("password") String password);
}
