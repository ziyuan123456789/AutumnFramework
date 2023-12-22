package org.example.mapper;

import org.example.Bean.Temp;
import org.example.Bean.User;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MyInsert;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MyMapper;
import org.example.FrameworkUtils.Annotation.MyParam;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MySelect;

import java.util.List;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyMapper
public interface UserMapper {
    @MySelect("select username,password from user where username=#{username} and password=#{password}")
    User login(@MyParam("username") String username,@MyParam("password") String password);
    @MyInsert("insert into  user (username,password) values (#{username},#{password})")
    Integer insertUser(@MyParam("username") String username,@MyParam("password") String password);
}
