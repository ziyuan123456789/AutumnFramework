package org.example.Config;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.example.Bean.Car;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.Orm.MineBatis.Io.Resources;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSession;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactory;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactoryBuilder;
import org.example.controller.AutumnTestController;
import org.example.mapper.UserMapper;

import java.beans.PropertyVetoException;
import java.io.InputStream;

/**
 * @author ziyuan
 * @since 2024.04
 */
@MyConfig
@Slf4j
public class BeanTestConfig {

    @MyAutoWired
    UserMapper userMapper;
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

    @AutumnBean
    public SqlSession getMapper() throws PropertyVetoException, DocumentException {
        InputStream inputStream = Resources.getResourceAsSteam("minebatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        return sqlSessionFactory.openSession();
    }

}
