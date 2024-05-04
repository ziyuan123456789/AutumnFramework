package org.example.Config;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.example.Bean.Car;
import org.example.FrameworkUtils.AutumnMVC.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConfig;
import org.example.FrameworkUtils.Orm.MineBatis.Io.Resources;
import org.example.FrameworkUtils.Orm.MineBatis.session.DefaultSqlSession;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSession;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactory;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactoryBuilder;

import java.beans.PropertyVetoException;
import java.io.InputStream;

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

    @AutumnBean
    public DefaultSqlSession getMapper() throws PropertyVetoException, DocumentException {
        InputStream inputStream = Resources.getResourceAsSteam("minebatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return (DefaultSqlSession) sqlSession;
    }

}
