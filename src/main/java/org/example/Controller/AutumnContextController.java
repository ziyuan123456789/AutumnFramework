package org.example.Controller;

import lombok.extern.slf4j.Slf4j;
import org.example.Bean.Car;
import org.example.Config.BeanTestConfig;
import org.example.Config.Test;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;
import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.ContextClosedEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationListener;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2025.06
 */
@MyController
@MyRequestMapping("/context")
@Slf4j
public class AutumnContextController implements BeanFactoryAware, ApplicationListener<ContextClosedEvent> {

    //测试自身循环依赖
    @MyAutoWired
    private AutumnContextController autumnContextController;


    private ApplicationContext beanFactory;


    //测试配置文件注入器
    @Value("url")
    private String sqlUrl;

    @MyAutoWired("postProcessChange")
    private Car car;

    @MyAutoWired
    private Test test;

    @MyAutoWired
    private BeanTestConfig beanTestConfig;

    @MyRequestMapping("/factory")
    public String getBeanFactory() {
        return beanFactory.toString();
    }

    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.beanFactory = beanFactory;
    }

    //获取字典树
    @MyRequestMapping("/getMappingTree")
    public String getMappingTree() {
        return beanFactory.getBean("urlMappingTree").toString();
    }

    //循环依赖测试
    @MyRequestMapping("/cycleTest")
    public Map<String, Object> cycleTest() {
        return autumnContextController.mapTest();
    }

    @MyRequestMapping("/getBean")
    public String getBean() {
        log.debug(beanTestConfig.getClass().getName());
        return beanTestConfig.giveMeWenJieCar().toString();
    }

    //测试@Bean("BeanName")功能是否正常,同时看看Json解析器好不好用
    @MyRequestMapping("/map")
    public Map<String, Object> mapTest() {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put("url", sqlUrl);
        log.info(car.toString());
        log.info(test.toString());
        return myMap;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info(("接受到容器关闭信号"));

    }

    @Override
    public boolean supportsEvent(ApplicationEvent event) {
        return event instanceof ContextClosedEvent;
    }


}
