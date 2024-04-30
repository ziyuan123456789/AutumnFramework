package org.example.Config;

import lombok.extern.slf4j.Slf4j;
import org.example.Bean.Car;
import org.example.FrameworkUtils.AutumnMVC.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConfig;

/**
 * @author ziyuan
 * @since 2024.04
 */
@MyConfig
@Slf4j
public class BeanTestConfig {
    @AutumnBean("BYD")
    public Car giveMeBydCar(){
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
