package org.example.FrameworkUtils.AutumnCore.BeanLoader;

import lombok.Data;
import lombok.ToString;
import org.example.FrameworkUtils.AutumnCore.Annotation.DependsOn;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Aop.AutumnAopFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.FactoryBean;
import org.example.FrameworkUtils.Exception.BeanDefinitionCreationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
  @author ziyuan
 * @since 2024.1
 */

/**
 * 一个美丽的花瓶,什么东西都要有个包装更好,但愿你心爱的女孩也有
 */
@Data
@ToString
public class MyBeanDefinition {

    //指定依赖顺序
    private List<String> dependsOn = new ArrayList<>();

    private AnnotationMetadata metadata;

    private String name;

    private Class<?> beanClass;

    //错误用法,已经弃用
    @Deprecated
    private boolean isStarter = false;

    //错误用法,已经弃用
    @Deprecated
    private ObjectFactory<?> starterMethod;

    //xxx:是谁成产了这个@bean
    private Class<?> configurationClass;

    private Object instance = null;

    private Method doMethod;


    //其实这个构造器和参数可以包装一下,成为一个新的容器类 但是懒得搞了
    private Constructor<?> constructor;

    private Object[] parameters = new Object[0];

    private Set<String> initMethodName = new HashSet<>();

    private Set<String> afterMethodName = new HashSet<>();

    private Set<Method> initMethod = new HashSet<>();

    private Set<Method> afterMethod = new HashSet<>();

    private Scope scope = Scope.SINGLETON;

    public Boolean isFactoryBean() {
        if (beanClass.getName().contains(AutumnAopFactory.CGLIB_MARK)) {
            beanClass = beanClass.getSuperclass();
        }
        return FactoryBean.class.isAssignableFrom(beanClass);
    }

    public MyBeanDefinition(String name, Class<?> beanClass) {
        this.metadata = new AnnotationMetadata(beanClass);
        this.name = name;
        this.beanClass = beanClass;
        //在包装为beanDefinition的时候就扫描一下依赖,并保存一下构造器,但大概率构造器注入不会进行起用
        this.scanBeanDependsOn(beanClass);
    }

    public MyBeanDefinition(Class<?> beanClass) {
        this.metadata = new AnnotationMetadata(beanClass);
        this.name = beanClass.getName();
        this.beanClass = beanClass;
        this.scanBeanDependsOn(beanClass);
    }

    //无参构造方法,一切由你来决定
    public MyBeanDefinition( ){

    }

    private void scanBeanDependsOn(Class<?> beanClass) {

        if (beanClass.isInterface() || Modifier.isAbstract(beanClass.getModifiers())) {
            return;
        }

        if (beanClass.getName().contains(AutumnAopFactory.CGLIB_MARK)) {
            beanClass = beanClass.getSuperclass();
        }

        DependsOn depends = beanClass.getAnnotation(DependsOn.class);
        if (depends != null) {
            Collections.addAll(dependsOn, depends.value());
        }

        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();

        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(MyAutoWired.class)) {
                this.constructor = constructor;
                return;
            }

            if (constructor.getParameterCount() == 0) {
                this.constructor = constructor;
            }
        }

        if (this.constructor == null) {
            throw new BeanDefinitionCreationException("Class " + beanClass.getName() + " 必须提供一个无参构造器,否则请在构造器上添加 @MyAutoWired 注解"
            );
        }


    }


}

