package org.example.mapper;

import org.example.Bean.User;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyParam;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MyMapper;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MySelect;

import java.util.List;

/**
 * @author ziyuan
 * @since 2024.04
 */
@MyMapper
public interface UserMapper {
    @MySelect("select UserID,Password from user ")
    List<User> getAllUser();

    @MySelect("select * from user where UserName=#{UserName} and Password=#{Password} limit 1")
    User checkUser(@MyParam("UserName") String UserName, @MyParam("Password")String Password);
}
