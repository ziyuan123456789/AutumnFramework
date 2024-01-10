package org.example.Config;

import org.example.Bean.Car;
import org.example.FrameworkUtils.AutumnMVC.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConfig;
import org.example.mapper.CarMapper;
import org.example.mapper.TestMapper;
import org.example.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyConfig
public class beanConfig {

    @MyAutoWired
    CarMapper carMapper;
    @MyAutoWired
    UserMapper userMapper;

    @MyAutoWired
    TestMapper testMapper;
//    @AutumnBean("beanTest")
//    public String beanTest(){
//        return  "123";
//    }

    @AutumnBean("beanTest")
    public ArrayList<Car> beanTest(){
        return carMapper.getAllCarList();
    }

    @AutumnBean("beanTest2")
    public String beanTest2() {
        return "123456";
    }

}