package org.example.FrameworkUtils.AutumnCore.Ioc;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Aop.AutumnAopFactory;
import org.example.FrameworkUtils.AutumnCore.Aop.CgLibAop;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.ObjectFactory;
import org.example.FrameworkUtils.AutumnCore.compare.AnnotationInterfaceAwareOrderComparator;
import org.example.FrameworkUtils.AutumnCore.env.ApplicationArguments;
import org.example.FrameworkUtils.AutumnCore.env.Environment;
import org.example.FrameworkUtils.Exception.BeanCreationException;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ziyuan
 * @since 2025.01
 */

//正在施工中,稍安勿躁,其实应该再抽出一个abstractApplicationContext来做这些基础的事情,不过类膨胀更让人苦恼,还在现在这写吧
@Slf4j
public class AnnotationConfigApplicationContext implements ApplicationContext {


    //一级缓存存储成熟bean
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    // 二级缓存提前暴露的还未完全成熟的bean,用于解决循环依赖
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();


    // 三级缓存：对象工厂,创建Jdk代理/CgLib代理/配置类Bean/普通Bean
    private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>();

    private Environment environment;

    private final Map<String, MyBeanDefinition> beanDefinitionMap = new LinkedHashMap<>();

    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    private final List<BeanDefinitionRegistryPostProcessor> beanDefinitionRegistryPostProcessors = new ArrayList<>();


    private final List<InstantiationAwareBeanPostProcessor> instantiationAwareProcessors = new ArrayList<>();

    private final List<AutumnAopFactory> aopFactories = new LinkedList<>();


    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void refresh() {
        invokeBeanFactoryPostProcessors();


    }

    private void invokeBeanFactoryPostProcessors() {
        for (BeanFactoryPostProcessor bfp : beanFactoryPostProcessors) {
            if (bfp instanceof BeanDefinitionRegistryPostProcessor bdp) {
                beanDefinitionRegistryPostProcessors.add(bdp);
            }
        }
        AnnotationInterfaceAwareOrderComparator.getInstance().compare(beanFactoryPostProcessors);
        AnnotationInterfaceAwareOrderComparator.getInstance().compare(beanDefinitionRegistryPostProcessors);




    }

    @Override
    public void put(String key, Object value) {

    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public Map<String, Object> getIocContainer() {
        return Map.of();
    }

    @Override
    public Properties getProperties() {
        return null;
    }

    @Override
    public List<Object> getBeansByAnnotation(Class<? extends Annotation> annotationClass) {
        return List.of();
    }

    @Override
    public void addBean(String name, Object bean) {

    }

    @Override
    public <T> List<T> getBeansOfType(Class<T> type) {
        return List.of();
    }

    @Override
    public void registerShutdownHook() {

    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {

    }

    @Override
    public Object getBean(String name) {
        return doGetBean(name);
    }

    private Object doGetBean(String beanName) {
        Object sharedInstance = getSingleton(beanName, true);
        return sharedInstance;

    }


    private Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singletonObject = singletonObjects.get(beanName);
        //一级缓存找不到
        if (singletonObject == null) {
//            log.debug("一级缓存中找不到Bean,前往二级缓存查找: {}", beanName);
            singletonObject = earlySingletonObjects.get(beanName);
            if (singletonObject == null) {
//                log.debug("二级缓存中找不到Bean,前往三级缓存查找: {}", beanName);
                ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    try {
                        //在对象实例化之前给最后一次机会,可替换实现类,例如Aop的实现
                        singletonObject = doInstantiationAwareBeanPostProcessorBefore(beanName, singletonFactory);
                        //生产对象后更新缓存
                        earlySingletonObjects.put(beanName, singletonObject);
                        singletonFactories.remove(beanName);
                    } catch (Exception e) {
                        log.error(String.valueOf(e));
                        throw new BeanCreationException("创建Bean实例失败: " + beanName, e);
                    }
                }
            }
            if (singletonObject != null) {
                //更新一级缓存
                singletonObjects.put(beanName, singletonObject);
                //从二级缓存移除
                earlySingletonObjects.remove(beanName);
            }
        }
        return singletonObject;
    }

    private Object doInstantiationAwareBeanPostProcessorBefore(String beanName, ObjectFactory<?> factory) throws Exception {
        Object currentResult = null;
        Class<?> beanClass = beanDefinitionMap.get(beanName).getBeanClass();
        for (InstantiationAwareBeanPostProcessor processor : instantiationAwareProcessors) {
            if (processor instanceof CgLibAop) {
                if (beanName.contains("org.example.service.impl")) {
                    System.out.println(1);
                }
                currentResult = ((CgLibAop) processor).postProcessBeforeInstantiation(aopFactories, beanClass, beanName, currentResult);
            } else {
                Object result = processor.postProcessBeforeInstantiation(beanClass, beanName);
                if (result != null) {
                    currentResult = result;
                }
            }
        }
        return currentResult != null ? currentResult : factory.getObject();
    }



    @Override
    public <T> T getBean(Class<T> requiredType) {
        return null;
    }


    @Override
    public ApplicationArguments getApplicationArguments() {
        return environment.getApplicationArguments();
    }

    @Override
    public String getProperty(String key) {
        return environment.getProperty(key);
    }

    @Override
    public Properties getAllProperties() {
        return environment.getAllProperties();
    }

    @Override
    public String[] getActiveProfiles() {
        return environment.getActiveProfiles();
    }

    @Override
    public String[] getDefaultProfiles() {
        return environment.getDefaultProfiles();
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        if (singletonObjects.containsKey(beanName)) {
            throw new IllegalStateException("单例BEAN" + beanName + "已经存在了");
        }
        singletonObjects.put(beanName, singletonObject);
    }

    @Override
    public Object getSingleton(String beanName) {
        return this.getBean(beanName);
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return singletonObjects.containsKey(beanName);
    }

    @Override
    public List<String> getSingletonNames() {
        return new ArrayList<>(singletonObjects.keySet());
    }

    @Override
    public int getSingletonCount() {
        return singletonObjects.size();
    }

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
