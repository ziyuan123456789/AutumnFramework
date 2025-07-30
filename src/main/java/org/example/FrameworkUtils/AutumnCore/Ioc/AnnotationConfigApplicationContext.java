package org.example.FrameworkUtils.AutumnCore.Ioc;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyOrder;
import org.example.FrameworkUtils.AutumnCore.Aop.AutumnAopFactory;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.ObjectFactory;
import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEventMulticaster;
import org.example.FrameworkUtils.AutumnCore.Event.ContextClosedEvent;
import org.example.FrameworkUtils.AutumnCore.Event.ContextRefreshedEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationListener;
import org.example.FrameworkUtils.AutumnCore.Event.SimpleApplicationEventMulticaster;
import org.example.FrameworkUtils.AutumnCore.compare.AnnotationInterfaceAwareOrderComparator;
import org.example.FrameworkUtils.AutumnCore.env.ApplicationArguments;
import org.example.FrameworkUtils.AutumnCore.env.Environment;
import org.example.FrameworkUtils.Exception.BeanCreationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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


@Slf4j
@EqualsAndHashCode
public class AnnotationConfigApplicationContext implements ConfigurableApplicationContext {

    //一级缓存存储成熟bean
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    /**
     * 二级缓存提前暴露的还未完全成熟的bean,用于解决循环依赖
     * 三层缓存能生效的本质就是分离对象创建和属性注入,但构造器注入会导致这一步紧耦合,无法分离,所以三级缓存面对构造器注入会失效
     */
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();

    /**
     * 三级缓存：对象工厂
     * 原则上来说进需要二层缓存就可以解决循环依赖问题,但ObjectFactory存在的意义是为了AOP等替换Bean实现
     */
    private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>();

    /**
     * Bean定义Map
     * 在本项目中,我没有对Bean定义做任何抽象,也没有合并Bean定义相关的功能
     * 在创建Bean定义构造方法中会自动扫描这个类的一些元信息,包装为AnnotationMetadata
     * 所以不需要额外再手动处理注解,调用AnnotationMetadata的方法就可以
     */
    private final Map<String, MyBeanDefinition> beanDefinitionMap = new LinkedHashMap<>();

    /**
     * Bean工厂后置处理器,包含Bean定义后置处理器
     */
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    //Bean后置处理器
    private final Set<BeanPostProcessor> beanPostProcessors = new HashSet<>();

    //实例化前后处理器
    private final List<InstantiationAwareBeanPostProcessor> instantiationAwareProcessors = new ArrayList<>();

    /**
     * 正在创建的单例Bean
     * 依靠这张Set我们可以追溯完整的依赖关系
     * 依靠这个打印出循环依赖图
     */
    private final Set<String> singletonsCurrentlyInCreation = new HashSet<>();

    //Aop工厂
    private final List<AutumnAopFactory> aopFactories = new LinkedList<>();

    //工厂bean对象缓存
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();

    //依赖beanMap
    private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>();

    private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>();

    private final Set<String> registeredSingletons = Collections.synchronizedSet(new LinkedHashSet<>(256));

    @Getter
    private final Set<ApplicationListener<ApplicationEvent>> applicationListeners = new HashSet<>();

    //通用比较器 用于处理Order注解 Order接口等,并进行原地排序
    private final AnnotationInterfaceAwareOrderComparator annotationInterfaceAwareOrderComparator = AnnotationInterfaceAwareOrderComparator.getInstance();

    private ApplicationEventMulticaster applicationEventMulticaster;

    private Set<ApplicationEvent> earlyApplicationEvents;

    private Set<ApplicationListener<ApplicationEvent>> earlyApplicationListeners;

    //环境
    private Environment environment;

    private long startupDate;


    public static final String ANSI_RESET = "\u001B[0m";

    protected void onRefresh() {
    }

    protected void postProcessBeanFactory(ConfigurableApplicationContext beanFactory) {
    }


    private void finishRefresh() {
        this.publishEvent(new ContextRefreshedEvent(new Object()));
    }

    private void prepareBeanFactory(ConfigurableApplicationContext beanFactory) {
        /**
         * 其实我有点理解不来ApplicationContextAwareProcessor的作用 既然在invokeAwareMethods就已经可以setAware了,而且invokeAwareMethods可以处理的类远远比ApplicationContextAwareProcessor更多,更早
         * 任何经过spring创建的对象都应该可以被invokeAwareMethods处理,为何不直接增强invokeAwareMethods的功能呢?
         * 我只能解释为ApplicationContextAwareProcessor的功能更多更全
         */
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));


    }
    public static final String ANSI_BLACK = "\u001B[30m";

    private void registerListeners() {
        for (ApplicationListener<ApplicationEvent> listener : getApplicationListeners()) {
            addApplicationListener(listener);
        }

        for (MyBeanDefinition df : beanDefinitionMap.values()) {
            if (ApplicationListener.class.isAssignableFrom(df.getBeanClass())) {
                addApplicationListener((ApplicationListener<ApplicationEvent>) getBean(df.getName()));
            }
        }

    }


    public void addApplicationListener(ApplicationListener<ApplicationEvent> listener) {
        if (this.applicationEventMulticaster != null) {
            this.applicationEventMulticaster.addApplicationListener(listener);
        }
        this.applicationListeners.add(listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<ApplicationEvent> listener) {
        if (this.applicationEventMulticaster != null) {
            this.applicationEventMulticaster.removeApplicationListener(listener);
        }
        this.applicationListeners.remove(listener);
    }

    private void initApplicationEventMulticaster() {
        this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(this);
        registerSingleton("applicationEventMulticaster", this.applicationEventMulticaster);

    }


    //对容器中的对象进行全部getBean,因为Aop部分我参考Spring的实现,因此我需要单独初始化我的AutumnAopFactory
    private void finishBeanFactoryInitialization(ConfigurableApplicationContext fatory) {
        for (Map.Entry<String, MyBeanDefinition> entry : fatory.getBeanDefinitionMap().entrySet()) {
            String beanName = entry.getKey();
            doGetBean(beanName);
        }
    }

    //注册并实例化BeanPostProcessors
    private void registerBeanPostProcessors(ConfigurableApplicationContext configurableApplicationContext) {
        List<BeanPostProcessor> annoPriorityOrderedPostProcessors = new ArrayList<>();
        List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
        List<String> orderedPostProcessorNames = new ArrayList<>();
        List<String> nonOrderedPostProcessorNames = new ArrayList<>();

        for (Map.Entry<String, MyBeanDefinition> entry : configurableApplicationContext.getBeanDefinitionMap().entrySet()) {
            Class<?> beanClass = entry.getValue().getBeanClass();
            if (BeanPostProcessor.class.isAssignableFrom(beanClass)) {
                if (beanClass.isAnnotationPresent(MyOrder.class)) {
                    BeanPostProcessor beanPostProcessor = (BeanPostProcessor) configurableApplicationContext.getBean(entry.getKey());
                    annoPriorityOrderedPostProcessors.add(beanPostProcessor);
                }
                if (beanClass.isAssignableFrom(PriorityOrdered.class)) {
                    BeanPostProcessor beanPostProcessor = (BeanPostProcessor) configurableApplicationContext.getBean(entry.getKey());
                    priorityOrderedPostProcessors.add(beanPostProcessor);
                } else if (beanClass.isAssignableFrom(Ordered.class)) {
                    orderedPostProcessorNames.add(entry.getKey());
                } else {
                    nonOrderedPostProcessorNames.add(entry.getKey());
                }
            }
        }

        int beanProcessorTargetCount = beanPostProcessors.size() + 1 + annoPriorityOrderedPostProcessors.size() + priorityOrderedPostProcessors.size() + orderedPostProcessorNames.size() + nonOrderedPostProcessorNames.size();
//        this.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, postProcessorNames, beanProcessorTargetCount));

        annotationInterfaceAwareOrderComparator.compare(annoPriorityOrderedPostProcessors);
        registerBeanPostProcessors(annoPriorityOrderedPostProcessors);

        annotationInterfaceAwareOrderComparator.compare(priorityOrderedPostProcessors);
        registerBeanPostProcessors(priorityOrderedPostProcessors);


        List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
        for (String ppName : orderedPostProcessorNames) {
            BeanPostProcessor pp = (BeanPostProcessor) getBean(ppName);
            orderedPostProcessors.add(pp);
        }
        annotationInterfaceAwareOrderComparator.compare(orderedPostProcessors);
        registerBeanPostProcessors(orderedPostProcessors);


        List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
        for (String ppName : nonOrderedPostProcessorNames) {
            BeanPostProcessor pp = (BeanPostProcessor) getBean(ppName);
            nonOrderedPostProcessors.add(pp);
        }
        annotationInterfaceAwareOrderComparator.compare(nonOrderedPostProcessors);
        registerBeanPostProcessors(nonOrderedPostProcessors);
//        this.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
    }

    private void registerBeanPostProcessors(List<BeanPostProcessor> postProcessors) {
        for (BeanPostProcessor postProcessor : postProcessors) {
            addBeanPostProcessor(postProcessor);
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
        Set<String> beanFactoryPostProcessorHashSet = new HashSet<>();
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
                beanFactoryPostProcessorHashSet.add(entry.getKey());
            }
        }

        annotationInterfaceAwareOrderComparator.compare(beanFactoryPostProcessors);
        annotationInterfaceAwareOrderComparator.compare(processingQueue);

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
                                annotationInterfaceAwareOrderComparator.compare(processingQueue);
                                processingQueue.add(newPostProcessor);
                                newPostProcessorsAdded = true;
                            }
                        }
                    }
                }

            }
        }

        for (Map.Entry<String, MyBeanDefinition> entry : beanDefinitionMap.entrySet()) {
            Class<?> beanClass = entry.getValue().getBeanClass();
            if (BeanFactoryPostProcessor.class.isAssignableFrom(beanClass)) {
                if (!beanFactoryPostProcessorHashSet.contains(entry.getKey())) {
                    beanFactoryPostProcessors.add((BeanFactoryPostProcessor) getBean(entry.getKey()));
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

//    @Override
//    public Map<String, Object> getIocContainer() {
//        return Map.of();
//    }

    @Override
    public Properties getProperties() {
        return environment.getAllProperties();
    }

    @Override
    public List<Object> getBeansByAnnotation(Class<? extends Annotation> annotationClass) {
        return List.of();
    }
    public static final String ANSI_RED = "\u001B[31m";

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeanCreationException {
        return Map.of();
    }


    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.add(beanPostProcessor);
    }

    @Override
    public Set<BeanPostProcessor> getAllBeanPostProcessors() {
        return beanPostProcessors;
    }


    /**
     * 某些情况下使用beanFactory来进行getBean是一件危险的事情
     * 特别在自动注入的时机未到来的地方,例如(BeanDefinitionRegistryPostProcessor ,BeanFactoryPostProcessor,BeanPostProcessor)
     * 如果你使用Aware接口来获取beanFactory从而进行强制的getBean,那么会导致这个Bean被提前实例化,可能导致一些自定义的后置处理器无法处理这个Bean
     * 所以在getBean的时候请保证提前实例化这个Bean并不会对业务产生什么影响,你已经知晓这个Bean不可能被额外处理
     */
    @Override
    public Object getBean(String name) {
        return doGetBean(name);
    }

    private Object doGetBean(String name) {
        String beanName = transformedBeanName(name);
        Object beanInstance = null;
        Object sharedInstance = getSingleton(beanName, true);
        if (sharedInstance != null) {
            beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName);
        } else {
            if (!containsBeanDefinition(beanName)) {
                try {
                    for (MyBeanDefinition myBeanDefinition : beanDefinitionMap.values()) {
                        if (myBeanDefinition.isFactoryBean()) {
                            Class clazz = Class.forName(beanName);
                            Object o = getBean(myBeanDefinition.getName());
                            if (clazz.isAssignableFrom(o.getClass())) {
                                return o;
                            }

                        }
                    }
                    log.error(beanName);
                    throw new BeanCreationException();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                MyBeanDefinition mbd = getBeanDefinition(beanName);
                List<String> dependsOn = mbd.getDependsOn();
                if (dependsOn != null) {
                    for (String dep : dependsOn) {
                        registerDependentBean(dep, beanName);
                        getBean(dep);
                    }
                }

//                if (Scope.SINGLETON.equals(mbd.getScope())) {
                String finalBeanName = beanName;
                sharedInstance = getSingleton(beanName, () -> createBean(finalBeanName, mbd, null));
                beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName);
//                }
            } catch (Exception ignored) {
                throw new RuntimeException(ignored);
            }
        }
        return adaptBeanInstance(beanName, beanInstance);
    }

    private String transformedBeanName(String name) {
        String beanName = name;
        while (beanName.startsWith(FactoryBean.FACTORY_BEAN_PREFIX)) {
            beanName = beanName.substring(FactoryBean.FACTORY_BEAN_PREFIX.length());
        }
        return beanName;
    }


    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            synchronized (this.singletonObjects) {
                singletonObject = this.singletonObjects.get(beanName);
                if (singletonObject == null) {
                    beforeSingletonCreation(beanName);
                    try {
                        singletonObject = singletonFactory.getObject();
                        addSingleton(beanName, singletonObject);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        afterSingletonCreation(beanName);
                    }
                }
            }
        }
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
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private Object doCreateBean(String beanName, MyBeanDefinition mbd, Object[] args) {
        Object instance = createBeanInstance(beanName, mbd, args);
        if (isSingletonCurrentlyInCreation(beanName)) {
            addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, instance));
        }

        populateBean(beanName, mbd, instance);
        Object exposedObject = instance;
        exposedObject = initializeBean(beanName, exposedObject, mbd);
        return exposedObject;
    }

    private Object initializeBean(String beanName, Object bean, MyBeanDefinition mbd) {
        invokeAwareMethods(beanName, bean);
        Object wrappedBean = bean;
        wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
        try {
            invokeInitMethods(beanName, wrappedBean, mbd);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        return wrappedBean;

    }

    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) {

        Object result = existingBean;
        for (BeanPostProcessor processor : beanPostProcessors) {
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }


    private void invokeInitMethods(String beanName, Object wrappedBean, MyBeanDefinition mbd) {
        if (wrappedBean instanceof InitializingBean ib) {
            ib.afterPropertiesSet();
        }

        if (mbd.getInitMethod() != null) {
            try {
                for (Method method : mbd.getInitMethod()) {
                    method.invoke(wrappedBean);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

    }

    protected Object getEarlyBeanReference(String beanName, MyBeanDefinition mbd, Object bean) {
        Object exposedObject = bean;
        for (BeanPostProcessor bp : beanPostProcessors) {
            if (bp instanceof SmartInstantiationAwareBeanPostProcessor sibp) {
                exposedObject = sibp.getEarlyBeanReference(exposedObject, beanName);
            }
        }
        return exposedObject;
    }

    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        synchronized (this.singletonObjects) {
            if (!this.singletonObjects.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletonObjects.remove(beanName);
            }
        }
    }


    private void populateBean(String beanName, MyBeanDefinition mbd, Object instance) {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor ibp) {
                instance = ibp.postProcessProperties(instance, beanName);
            }
        }

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

        if (mbd.getDoMethod() != null) {
            Object configInstance = getBean(mbd.getConfigurationClass().getName());
            Method doMethod = mbd.getDoMethod();
            try {
                return doMethod.invoke(configInstance);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        if (mbd.getConstructor() != null) {
            Constructor<?> constructor = mbd.getConstructor();
            constructor.setAccessible(true);
            try {
                if (mbd.getParameters() == null || mbd.getParameters().length == 0) {
                    return constructor.newInstance();
                }
                return constructor.newInstance(mbd.getParameters());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new BeanCreationException(e);
            }
        }

        try {
            Constructor<?> constructor = mbd.getBeanClass().getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BeanCreationException(e);
        }
    }


    <T> T adaptBeanInstance(String name, Object bean) {

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

    protected Object getObjectForBeanInstance(Object beanInstance, String name, String beanName) {
        if (name != null && name.startsWith(FactoryBean.FACTORY_BEAN_PREFIX)) {
            if (!(beanInstance instanceof FactoryBean)) {
                throw new BeanCreationException("Bean name '" + beanName + "'不是一个factory bean");
            }
        }
        if (!(beanInstance instanceof FactoryBean<?>)) {
            return beanInstance;
        }
        Object object;
        FactoryBean<?> factoryBean = (FactoryBean<?>) beanInstance;
        try {
            object = getObjectFromFactoryBean(factoryBean, beanName);
            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName) throws Exception {
        if (factory.isSingleton() && containsSingleton(beanName)) {
            Object object = this.factoryBeanObjectCache.get(beanName);
            if (object == null) {
                object = factory.getObject();
                Object alreadyThere = this.factoryBeanObjectCache.get(beanName);
                if (alreadyThere != null) {
                    object = alreadyThere;
                } else {
                    if (object != null) {
                        this.factoryBeanObjectCache.put(beanName, object);
                    } else {
                        if (isSingletonCurrentlyInCreation(beanName)) {
                            return object;
                        }
                        beforeSingletonCreation(beanName);
                        try {
                            object = postProcessObjectFromFactoryBean(object, beanName);
                        } catch (Throwable ex) {
                            log.error("Error processing FactoryBean for bean: {}", beanName, ex);
                        } finally {
                            afterSingletonCreation(beanName);
                        }
                        if (object != null) {
                            this.factoryBeanObjectCache.put(beanName, object);
                        }
                    }
                }
            }
            return object;
        } else {
            Object object = factory.getObject();
            object = postProcessObjectFromFactoryBean(object, beanName);
            return object;
        }
    }

    private Object postProcessObjectFromFactoryBean(Object existingBean, String beanName) {
        Object result = existingBean;
        for (BeanPostProcessor processor : beanPostProcessors) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            singletonObject = this.earlySingletonObjects.get(beanName);
            if (singletonObject == null && allowEarlyReference) {
                beforeSingletonCreation(beanName);
                try {
                    ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        singletonObject = singletonFactory.getObject();
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        this.singletonFactories.remove(beanName);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    afterSingletonCreation(beanName);
                }
                return singletonObject;

            }
        }
        return singletonObject;
    }
    public static final String ANSI_GREEN = "\u001B[32m";




    protected Object resolveBeforeInstantiation(String beanName, MyBeanDefinition mbd) {

        Object bean = null;
        bean = applyBeanPostProcessorsBeforeInstantiation(mbd.getBeanClass(), beanName);
        if (bean != null) {
            bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
        }
        return bean;
    }

    //自定义实现BeanPostProcessor执行before方法
    protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor ibp) {
//                if (beanPostProcessor instanceof CgLibAop) {
//                    Object result = ((CgLibAop) beanPostProcessor).postProcessBeforeInstantiation(aopFactories, beanClass, beanName, null);
//                    if (result != null) {
//                        return result;
//                    }
//                }
                Object result = ibp.postProcessBeforeInstantiation(beanClass, beanName);
                if (result != null) {
                    return result;
                }
            }

        }
        return null;
    }

    //自定义实现BeanPostProcessor执行after方法
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeanCreationException {
        Object result = existingBean;
        for (BeanPostProcessor processor : beanPostProcessors) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }


    @Override
    public <T> T getBean(Class<T> requiredType) {
        Object resolved = resolveBean(requiredType);
        if (resolved == null) {
            for (MyBeanDefinition myBeanDefinition : beanDefinitionMap.values()) {
                if (myBeanDefinition.isFactoryBean()) {
                    Object o1 = getBean(myBeanDefinition.getName());
                    if (requiredType.isAssignableFrom(o1.getClass())) {
                        return (T) o1;
                    }

                }
            }
            throw new BeanCreationException("找实现类异常哦");
        }
        return (T) resolved;
    }

    private <T> T resolveBean(Class<T> requiredType) {
        List<String> impls = new ArrayList<>();
        for (MyBeanDefinition mb : beanDefinitionMap.values()) {
            if (requiredType.isAssignableFrom(mb.getBeanClass())) {
                impls.add(mb.getName());
            }
        }
        if (impls.isEmpty()) {
            return null;
        }
        if (impls.size() == 1) {
            //委托给getBean(String)
            return (T) getBean(impls.get(0));
        } else {
            throw new BeanCreationException(impls + "多个实现类");
        }

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
        if (!singletonsCurrentlyInCreation.add(beanName)) {
            logCircularDependency(beanName);
        }
        this.singletonsCurrentlyInCreation.add(beanName);
    }

    private void afterSingletonCreation(String beanName) {
        this.singletonsCurrentlyInCreation.remove(beanName);
    }


    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    @Override
    public void publishEvent(Object event) {
        applicationEventMulticaster.multicastEvent((ApplicationEvent) event);
    }

    @Override
    public void close() {
        this.publishEvent(new ContextClosedEvent("Bye"));
        this.destroyBeans();
    }

    private void destroyBeans() {
        for (MyBeanDefinition myBeanDefinition : beanDefinitionMap.values()) {
            if (myBeanDefinition.getAfterMethod() != null) {
                try {
                    for (Method method : myBeanDefinition.getAfterMethod()) {
                        method.invoke(getBean(myBeanDefinition.getName()));
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }


    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }


    @Override
    public void registerShutdownHook() {
    }
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BOLD = "\u001B[1m";

    //容器开始刷新
    @Override
    public void refresh() {
        log.info(ANSI_BOLD + ANSI_GREEN + "==== 容器开始refresh ====" + ANSI_RESET);

        //刷新前的准备,记录当前时间戳
        prepareRefresh();
        log.info(ANSI_YELLOW + "==== prepareRefresh阶段结束, 容器刷新前做准备, 创建一些高人一等的EarlyApplicationListeners ====" + ANSI_RESET);

        //BeanFactory前准备
        prepareBeanFactory(this);
        log.info(ANSI_YELLOW + "==== prepareBeanFactory阶段结束, 创建ApplicationContextAwareProcessor ====" + ANSI_RESET);

        try {
            //模板方法
            postProcessBeanFactory(this);
            log.info(ANSI_YELLOW + "==== postProcessBeanFactory阶段结束, 本项目没有制作扩展 ====" + ANSI_RESET);

            //调用BeanFactory后置处理器,因为 ConfigurationClassPostProcessor 的存在,绝大部分Bean定义包装完成
            invokeBeanFactoryPostProcessors();
            log.info(ANSI_YELLOW + "==== invokeBeanFactoryPostProcessors阶段结束, 调用BeanFactory后置处理器, 允许引入更多BeanDefinition ====" + ANSI_RESET);

            //注册实例化Bean后置处理器
            registerBeanPostProcessors(this);
            log.info(ANSI_YELLOW + "==== registerBeanPostProcessors阶段结束, AOP处理器等在此处被注册 ====" + ANSI_RESET);

            //初始化事件广播器
            initApplicationEventMulticaster();
            log.info(ANSI_YELLOW + "==== initApplicationEventMulticaster阶段结束, 初始化事件广播器 ====" + ANSI_RESET);

            //模板方法, Web容器初始化
            onRefresh();
            log.info(ANSI_YELLOW + "==== onRefresh阶段结束, Web容器初始化 ====" + ANSI_RESET);

            //注册监听器
            registerListeners();
            log.info(ANSI_YELLOW + "==== registerListeners阶段结束, 注册监听器 ====" + ANSI_RESET);

            //实例化所有的Bean
            finishBeanFactoryInitialization(this);
            log.info(ANSI_YELLOW + "==== finishBeanFactoryInitialization阶段结束, 所有非懒加载单例Bean均被初始化 ====" + ANSI_RESET);

            //刷新完成
            finishRefresh();
            log.info(ANSI_BOLD + ANSI_GREEN + "==== 容器refresh结束, 刷新完成 ====" + ANSI_RESET);

        } catch (Exception e) {
            log.error(ANSI_RED + "容器刷新失败: {}" + ANSI_RESET, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void prepareRefresh() {
        /**
         * 在AutumnApplication的run方法中,env已经生成,所以无需二次生成,spring的这种设计是为了防止你直接创建一个context出来而并非AutumnApplication来生成
         * 因此需要检测env是否存在,没有的话重新创建一个
         */
        this.startupDate = System.currentTimeMillis();
        if (this.earlyApplicationListeners == null) {
            this.earlyApplicationListeners = new LinkedHashSet<>(this.applicationListeners);
        } else {
            //refresh阶段可以二次调用,因此存在earlyApplicationListeners不为null的情况
            this.applicationListeners.clear();
            this.applicationListeners.addAll(this.earlyApplicationListeners);
        }
        this.earlyApplicationEvents = new HashSet<>();
    }

    @Override
    public void addBean(String name, Object bean) {
        if (bean == null) {
            throw new IllegalArgumentException("你怎么传递个空????????");
        }
        String beanName = transformedBeanName(name);
        addSingleton(beanName, bean);
    }

    private void logCircularDependency(String beanName) {
        List<String> creationList = new ArrayList<>(singletonsCurrentlyInCreation);
        if (creationList.size() == 1) {
            String sb = "\n" + "┌──->──┐\n" +
                    "|  " + beanName + "\n" +
                    "└──<-──┘";
            log.error(sb);
            return;
        }
        StringBuilder sb2 = new StringBuilder("\n");
        sb2.append("┌──->──┐\n");
        for (String string : creationList) {
            sb2.append("|  ").append(string).append("\n");
        }
        sb2.append("└──<-──┘");
        log.error(sb2.toString());
    }

}
