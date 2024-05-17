package org.example.FrameworkUtils.AutumnCore.Ioc;

import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.Exception.BeanCreationException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ziyuan
 * @since 2024.05
 */
public class SimpleMyBeanDefinitionRegistry implements BeanDefinitionRegistry {
    private final Map<String, MyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    @Override
    public void registerBeanDefinition(String beanName, MyBeanDefinition beanDefinition) throws BeanCreationException {
        if (beanDefinitionMap.containsKey(beanName)) {
            throw new BeanCreationException("Bean名称 '" + beanName + "' 已经被使用。");
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public void removeBeanDefinition(String beanName) throws BeanCreationException {
        MyBeanDefinition beanDefinition = beanDefinitionMap.remove(beanName);
        if (beanDefinition == null) {
            throw new BeanCreationException("没有找到Bean定义: " + beanName);
        }
    }

    @Override
    public MyBeanDefinition getBeanDefinition(String beanName) throws BeanCreationException {
        MyBeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new BeanCreationException("没有找到Bean定义: " + beanName);
        }
        return beanDefinition;
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    @Override
    public int getBeanDefinitionCount() {
        return beanDefinitionMap.size();
    }

    @Override
    public boolean isBeanNameInUse(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }
    @Override
    public Map<String, MyBeanDefinition> getBeanDefinitionMap() {
        return beanDefinitionMap;
    }
}
