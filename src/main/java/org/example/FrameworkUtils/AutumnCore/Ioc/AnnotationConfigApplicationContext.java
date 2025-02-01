package org.example.FrameworkUtils.AutumnCore.Ioc;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Aop.AutumnAopFactory;
import org.example.FrameworkUtils.AutumnCore.Aop.CgLibAop;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.ObjectFactory;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.Scope;
import org.example.FrameworkUtils.AutumnCore.compare.AnnotationInterfaceAwareOrderComparator;
import org.example.FrameworkUtils.AutumnCore.env.ApplicationArguments;
import org.example.FrameworkUtils.AutumnCore.env.Environment;
import org.example.FrameworkUtils.Exception.BeanCreationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
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


    private final List<InstantiationAwareBeanPostProcessor> instantiationAwareProcessors = new ArrayList<>();

    private final Set<String> singletonsCurrentlyInCreation = new HashSet<>();

    private final List<AutumnAopFactory> aopFactories = new LinkedList<>();

    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();

    private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>();

    private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>();

    private final Set<String> registeredSingletons = Collections.synchronizedSet(new LinkedHashSet<>(256));


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
        try {
            invokeBeanFactoryPostProcessors();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }

    /**
     * springboot中这个方法实现的太麻烦了,这一块我简化了一下,不和他写一样了
     * 本质上来说就是允许BeanDefinitionRegistryPostProcessor继续引入BeanDefinitionRegistryPostProcessor 其他的BeanDefinitionRegistryPostProcessor也可以继续引入..以此类推
     * 而且还得保证顺序的准确
     */

    private void invokeBeanFactoryPostProcessors() throws Exception {
        AnnotationScanner scanner = new AnnotationScanner();

        Queue<BeanDefinitionRegistryPostProcessor> processingQueue = new LinkedList<>();
        Set<BeanDefinitionRegistryPostProcessor> processedRegistryPostProcessors = new HashSet<>();
        for (BeanFactoryPostProcessor bfp : beanFactoryPostProcessors) {
            if (bfp instanceof BeanDefinitionRegistryPostProcessor bdp) {
                processingQueue.add(bdp);
            }
        }

        for (Map.Entry<String, MyBeanDefinition> entry : beanDefinitionMap.entrySet()) {
            Class<?> beanClass = entry.getValue().getBeanClass();
            if (BeanDefinitionRegistryPostProcessor.class.isAssignableFrom(beanClass)) {
                processingQueue.add((BeanDefinitionRegistryPostProcessor) getBean(entry.getKey()));
            }
            if (BeanFactoryPostProcessor.class.isAssignableFrom(beanClass)) {
                beanFactoryPostProcessors.add((BeanFactoryPostProcessor) getBean(entry.getKey()));
            }
        }

        AnnotationInterfaceAwareOrderComparator.getInstance().compare(beanFactoryPostProcessors);
        AnnotationInterfaceAwareOrderComparator.getInstance().compare(processingQueue);

        boolean newPostProcessorsAdded = true;

        while (newPostProcessorsAdded) {
            newPostProcessorsAdded = false;
            int size = processingQueue.size();
            for (int i = 0; i < size; i++) {
                BeanDefinitionRegistryPostProcessor postProcessor = processingQueue.poll();
                if (!processedRegistryPostProcessors.contains(postProcessor)) {
                    processedRegistryPostProcessors.add(postProcessor);
                    if (postProcessor != null) {
                        postProcessor.postProcessBeanDefinitionRegistry(scanner, this);
                    }
                    for (Map.Entry<String, MyBeanDefinition> entry : beanDefinitionMap.entrySet()) {
                        Class<?> beanClass = entry.getValue().getBeanClass();
                        if (BeanDefinitionRegistryPostProcessor.class.isAssignableFrom(beanClass)) {
                            BeanDefinitionRegistryPostProcessor newPostProcessor = (BeanDefinitionRegistryPostProcessor) getBean(entry.getKey());
                            if (!processedRegistryPostProcessors.contains(newPostProcessor)) {
                                AnnotationInterfaceAwareOrderComparator.getInstance().compare(processingQueue);
                                processingQueue.add(newPostProcessor);
                                newPostProcessorsAdded = true;
                            }
                        }
                    }
                }

            }
        }

        for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
            postProcessor.postProcessBeanFactory(scanner, this);
        }


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
        Object beanInstance = null;
        Object sharedInstance = getSingleton(beanName, true);
        if (sharedInstance != null) {
            //看看是不是factoryBean
            beanInstance = getObjectForBeanInstance(sharedInstance, beanName);
        } else {
            try {
                MyBeanDefinition mbd = getBeanDefinition(beanName);
                List<String> dependsOn = mbd.getDependsOn();
                if (dependsOn != null) {
                    for (String dep : dependsOn) {
                        registerDependentBean(dep, beanName);
                        doGetBean(dep);
                    }
                }

                if (Scope.SINGLETON.equals(mbd.getScope())) {
                    sharedInstance = getSingleton(beanName, () -> createBean(beanName, mbd, null));
                    beanInstance = getObjectForBeanInstance(sharedInstance, beanName);
                }
            } catch (Exception ignored) {
            }
        }
        return adaptBeanInstance(beanName, beanInstance);
    }


    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            beforeSingletonCreation(beanName);
            try {
                singletonObject = singletonFactory.getObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            addSingleton(beanName, singletonObject);
        }

        afterSingletonCreation(beanName);


        return singletonObject;
    }

    protected void addSingleton(String beanName, Object singletonObject) {
        Object oldObject = this.singletonObjects.putIfAbsent(beanName, singletonObject);
        if (oldObject != null) {
            throw new IllegalStateException("Could not register object [" + singletonObject + "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
        }
        this.singletonFactories.remove(beanName);
        this.earlySingletonObjects.remove(beanName);
        this.registeredSingletons.add(beanName);
    }


    private Object createBean(String beanName, MyBeanDefinition mbd, Object[] args) {

        Object bean = resolveBeforeInstantiation(beanName, mbd);
        if (bean != null) {
            return bean;
        }

        try {
            return doCreateBean(beanName, mbd, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object doCreateBean(String beanName, MyBeanDefinition mbd, Object[] args) {
        Object instance = createBeanInstance(beanName, mbd, args);
        invokeAwareMethods(beanName, instance);
//        populateBean(beanName, mbd, instanceWrapper);
//        exposedObject = initializeBean(beanName, exposedObject, mbd);
        return instance;
    }

    private void invokeAwareMethods(String beanName, Object bean) {

        if (bean instanceof BeanNameAware beanNameAware) {
            beanNameAware.setBeanName(beanName);
        }
        if (bean instanceof EnvironmentAware environmentAware) {
            environmentAware.setEnvironment(environment);
        }
        if (bean instanceof BeanFactoryAware beanFactoryAware) {
            beanFactoryAware.setBeanFactory(this);
        }

    }

    private Object createBeanInstance(String beanName, MyBeanDefinition mbd, Object[] args) {
        if (mbd.getConstructor() != null) {
            Constructor<?> constructor = mbd.getConstructor();
            try {
                return constructor.newInstance(args);
            } catch (Exception e) {
                try {
                    return constructor.newInstance(mbd.getParameters());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

        }
        throw new RuntimeException();
    }

    <T> T adaptBeanInstance(String name, Object bean) {
//        if (requiredType != null && !requiredType.isInstance(bean)) {
//            try {
//                Object convertedBean = getTypeConverter().convertIfNecessary(bean, requiredType);
//                if (convertedBean == null) {
//                    throw new RuntimeException();
//                }
//                return (T) convertedBean;
//            } catch (Exception ex) {
//                throw new RuntimeException();
//            }
//        }
        return (T) bean;
    }


    public void registerDependentBean(String beanName, String dependentBeanName) {
        Set<String> dependentBeans = this.dependentBeanMap.computeIfAbsent(beanName, k -> new LinkedHashSet<>(8));
        if (!dependentBeans.add(dependentBeanName)) {
            return;
        }

        Set<String> dependenciesForBean = this.dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k -> new LinkedHashSet<>(8));
        dependenciesForBean.add(beanName);

    }

    protected Object getObjectForBeanInstance(Object beanInstance, String beanName) {

        if (!(beanInstance instanceof FactoryBean<?> factoryBean)) {
            return beanInstance;
        }

        try {
            return factoryBean.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singletonObject = this.singletonObjects.get(beanName);
        //一级缓存找不到
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            singletonObject = this.earlySingletonObjects.get(beanName);
            if (singletonObject == null) {
                beforeSingletonCreation(beanName);
                singletonObject = this.earlySingletonObjects.get(beanName);
                if (singletonObject == null) {
                    try {
                        ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                        if (singletonFactory != null) {
                            singletonObject = singletonFactory.getObject();
                            if (this.singletonFactories.remove(beanName) != null) {
                                this.earlySingletonObjects.put(beanName, singletonObject);
                            } else {
                                singletonObject = this.singletonObjects.get(beanName);
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        afterSingletonCreation(beanName);
                    }


                }
                return singletonObject;
            }
        }
        return singletonObject;
    }


    protected Object resolveBeforeInstantiation(String beanName, MyBeanDefinition mbd) {
        Object bean = null;
        Class<?> targetType = mbd.getBeanClass();
        for (InstantiationAwareBeanPostProcessor processor : instantiationAwareProcessors) {
            if (processor instanceof CgLibAop) {
                bean = ((CgLibAop) processor).postProcessBeforeInstantiation(aopFactories, targetType, beanName, bean);
            } else {
                Object result = processor.postProcessBeforeInstantiation(targetType, beanName);
                if (result != null) {
                    bean = result;
                }
            }
        }

        return bean;
    }


    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    private Object doInstantiationAwareBeanPostProcessorBefore(String beanName, ObjectFactory<?> factory) throws Exception {
        Object currentResult = null;
        Class<?> beanClass = beanDefinitionMap.get(beanName).getBeanClass();
        for (InstantiationAwareBeanPostProcessor processor : instantiationAwareProcessors) {
            if (processor instanceof CgLibAop) {
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

    private void beforeSingletonCreation(String beanName) {
        this.singletonsCurrentlyInCreation.add(beanName);
    }


    private void afterSingletonCreation(String beanName) {
        this.singletonsCurrentlyInCreation.remove(beanName);
    }
}
