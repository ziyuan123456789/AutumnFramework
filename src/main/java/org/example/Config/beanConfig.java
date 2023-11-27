package org.example.Config;
import org.example.Bean.User;
import org.example.FrameworkUtils.Annotation.AutunmnBean;
import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyConfig;
import org.example.mapper.UserMapper;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
@MyConfig
public class beanConfig {
    @MyAutoWired
    UserMapper userMapper;
    @AutunmnBean
    public User beanTest(){
        return  userMapper.login("wzy","123");
    }

}