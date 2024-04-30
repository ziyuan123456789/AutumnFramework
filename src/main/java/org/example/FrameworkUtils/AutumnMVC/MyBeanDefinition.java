package org.example.FrameworkUtils.AutumnMVC;

import lombok.Data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author ziyuan
 * @since 2024.1
 */
@Data
public class MyBeanDefinition {
    private  String name;

    private boolean isCglib;

    private Class<?> beanClass;
    //xxx:是谁成产了这个@bean
    private Class<?> configurationClass;

    private Object instance = null;

    private Method doMethod;

    private Constructor<?> constructor;

    private String initMethodName;

    private String afterMethodName;

    private Method initMethod;

    private Method afterMethod;

    public MyBeanDefinition(String name, Class<?> beanClass){
        this.name = name;
        this.beanClass = beanClass;
    }

    public MyBeanDefinition() {

    }
}
