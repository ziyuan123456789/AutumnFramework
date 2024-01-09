package org.example.Config;

import org.example.Bean.Temp;
import org.example.Bean.User;
import org.example.FrameworkUtils.AutumnMVC.Annotation.AutunmnBean;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConfig;
import org.example.mapper.TestMapper;
import org.example.mapper.UserMapper;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyConfig
public class beanConfig {
    @MyAutoWired
    UserMapper userMapper;

    @MyAutoWired
    TestMapper testMapper;
//    @AutunmnBean
//    public User beanTest(){
//        return  userMapper.login("wzy","123");
//    }
//
//    @AutunmnBean
//    public Temp beanTest1() {
//        return testMapper.selectById1(1);
//    }

}