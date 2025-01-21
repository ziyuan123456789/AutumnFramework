package org.example.FrameworkUtils.AutumnCore.Ioc;

import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.Exception.BeanDefinitionCreationException;

import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.05
 */

/**
 * BeanDefinition注册表接口
 */
public interface BeanDefinitionRegistry {

    //xxx: 注册一个新的BeanDefinition
    void registerBeanDefinition(String beanName, MyBeanDefinition beanDefinition) throws BeanDefinitionCreationException;

    //xxx: 移除一个指定名称的BeanDefinition
    void removeBeanDefinition(String beanName) throws BeanDefinitionCreationException;

    //xxx: 获取一个指定名称的BeanDefinition
    MyBeanDefinition getBeanDefinition(String beanName) throws BeanDefinitionCreationException;

    //xxx: 判断是否包含一个指定名称的BeanDefinition
    boolean containsBeanDefinition(String beanName);

    //xxx: 获取所有已注册的BeanDefinition的名称
    String[] getBeanDefinitionNames();

    //xxx:获取已注册的BeanDefinition的数量
    int getBeanDefinitionCount();

    //xxx: 判断给定的Bean名称是否已经在使用
    boolean isBeanNameInUse(String beanName);

    //xxx: 获取所有已注册的BeanDefinition
    Map<String, MyBeanDefinition> getBeanDefinitionMap();
}
