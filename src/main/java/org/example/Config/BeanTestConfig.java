package org.example.Config;

import lombok.extern.slf4j.Slf4j;
import org.example.Bean.Car;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPostConstruct;
import org.example.mapper.UserMapper;

/**
 * @author ziyuan
 * @since 2024.04
 */
@MyConfig
@Slf4j
public class BeanTestConfig {


    @MyAutoWired
    UserMapper userMapper;

    @MyPostConstruct
    public void init(){
        try{
            log.warn(userMapper.getAllUser(1).toString());
        }catch (Exception e){
            log.warn("sql执行失败了,但没关系,继续运行");
        }

    }
    @AutumnBean("BYD")
    public Car giveMeBydCar() throws Exception {
        Car car=new Car();
        car.setName("BYD");
        return car;
    }

    @AutumnBean("WenJie")
    public Car giveMeWenJieCar(){
        Car car=new Car();
        car.setName("WenJie");
        return car;
    }


}
