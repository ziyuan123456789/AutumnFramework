package org.example.FrameworkUtils.AutumnCore.BeanLoader;

import lombok.Data;
import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnCommander;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author ziyuan
 * @since 2024.1
 */
@Data
public class MyBeanDefinition {
    private  String name;

    private Class<?> beanClass;

    private boolean isStarter = false;

    private AutumnCommander commander;

    private ObjectFactory<?> starterMethod;
    //xxx:是谁成产了这个@bean
    private Class<?> configurationClass;

    private Object instance = null;

    private Method doMethod;

    private Constructor<?> constructor;

    private String initMethodName;

    private String afterMethodName;

    private Method initMethod;

    private Method afterMethod;

    private Scope scope;

    public MyBeanDefinition(String name, Class<?> beanClass){
        this.name = name;
        this.beanClass = beanClass;
    }

    public MyBeanDefinition() {

    }
}

enum Scope {
    SINGLETON,
    PROTOTYPE,
    REQUEST,
    SESSION,
    GLOBAL,
    THREAD
}

