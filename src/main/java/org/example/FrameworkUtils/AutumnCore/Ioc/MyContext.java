package org.example.FrameworkUtils.AutumnCore.Ioc;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.Lazy;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAspect;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;
import org.example.FrameworkUtils.AutumnCore.Aop.AutumnAopFactory;
import org.example.FrameworkUtils.AutumnCore.Aop.AutumnRequestProxyFactory;
import org.example.FrameworkUtils.AutumnCore.Aop.CgLibAop;
import org.example.FrameworkUtils.AutumnCore.Aop.LazyBeanFactory;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.ObjectFactory;
import org.example.FrameworkUtils.AutumnCore.env.Environment;
import org.example.FrameworkUtils.Exception.BeanCreationException;
import org.example.FrameworkUtils.PropertiesReader.PropertiesReader;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wsh
 */


/**
 * 孩子们,到这里MyContext的使命就已经走到尾声了
 * 我依稀还记得这个类最开始的样子: Map<Class, Object>,那时我花了两天时间看B站网课,完成了依赖扫描和简陋的依赖注入,那种跑起来的成就感依然让我兴奋
 * 虽然MyContext非常简陋,充满了各种不可名状的补丁,以至于我自己都很难看懂它,但它依然挺厉害的,它实现了许多功能——
 * 比如完整的生命周期,自动装配机制,解决循环依赖的能力,以及带AOP链的依赖注入等,SpringBoot的常见功能杂七杂八地实现了大半
 * 然而,技术债,永远是那个无法绕过去的幽灵.不断在一个不稳定 不合理的架构上打补丁,终究是没有尽头的
 * 于是,我决定让MyContext退场,迎接我们的新朋友——`AnnotationConfigApplicationContext`,它将代替MyContext继续走下去
 * 俗话说：一将功成万骨枯 人人都知道AnnotationConfigApplicationContext的`refresh`和`doGetBean`,但背后默默无闻的基类`BeanFactory`又有谁真正研究过呢
 * 日日夜夜的奋斗,成就了一个又一个看似平凡的瞬间.每一行代码,每一段逻辑,都有其不可言说的深意
 * 让我们怀念MyContext,也让我们欢迎新的时代
 * 2025/1/20
 */
@Slf4j
public class MyContext implements ApplicationContext {
    private static volatile MyContext instance;

    private MyContext() {
    }

    private static MyContext getInstance() {
        if (instance == null) {
            synchronized (MyContext.class) {
                if (instance == null) {
                    instance = new MyContext();
                }
            }
        }
        return instance;
    }

    private final Map<String, Object> sharedMap = new ConcurrentHashMap<>();
    private List<InstantiationAwareBeanPostProcessor> instantiationAwareProcessors = new ArrayList<>();
    private List<BeanPostProcessor> regularPostProcessors = new ArrayList<>();
    private List<AutumnAopFactory> aopFactories = new LinkedList<>();
    private final List<Map<Method,Object>> afterMethods=new ArrayList<>();
    //xxx:一级缓存存储成熟bean
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    //xxx: 二级缓存提前暴露的还未完全成熟的bean,用于解决循环依赖
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();
    private final Map<String, MyBeanDefinition> beanDefinitions = new ConcurrentHashMap<>();
    //xxx: 三级缓存：对象工厂,创建Jdk代理/CgLib代理/配置类Bean/普通Bean
    private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>();
    private final PropertiesReader propertiesReader = new PropertiesReader();
    private final Properties properties = propertiesReader.initProperties();
    private List<Class<?>> cycleSet=new ArrayList<>();
    private final Map<String,List<Class<?>>> beanDependencies=new HashMap<>();

    private Environment environment;

    private int times=1;


    @Override
    public Object getBean(String beanName) {
        try {
            if (times != 1) {
                cycleSet.add(Class.forName(beanName));
            }
            times++;
        } catch (ClassNotFoundException e) {
        }

        // xxx:寻找一级缓存
        Object singletonObject = singletonObjects.get(beanName);

        // xxx:一级缓存找不到
        if (singletonObject == null) {
//            log.debug("一级缓存中找不到Bean,前往二级缓存查找: {}", beanName);
            // xxx:二级缓存寻找
            singletonObject = earlySingletonObjects.get(beanName);
            if (singletonObject == null) {
//                log.debug("二级缓存中找不到Bean,前往三级缓存查找: {}", beanName);
                // xxx:二级缓存中找不到
                // xxx: 从三级缓存获取工厂方法
                ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    try {
                        //xxx:在对象实例化之前给最后一次机会,可替换实现类,例如Aop的实现
                        singletonObject = doInstantiationAwareBeanPostProcessorBefore(beanName, singletonFactory);
                        //xxx:生产对象后更新缓存
                        earlySingletonObjects.put(beanName, singletonObject);
                        singletonFactories.remove(beanName);
                    } catch (Exception e) {
                        log.error(String.valueOf(e));
                        throw new BeanCreationException("创建Bean实例失败: " + beanName, e);
                    }
                }
            }
            if (singletonObject != null) {
                //xxx:更新一级缓存
                singletonObjects.put(beanName, singletonObject);
                //xxx:从二级缓存移除
                earlySingletonObjects.remove(beanName);
            }
        }
        return singletonObject;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        for (Object bean : singletonObjects.values()) {
            if (requiredType.isInstance(bean)) {
                return requiredType.cast(bean);
            }
        }
        throw new BeanCreationException("找不到类型为" + requiredType.getName() + "的Bean");
    }


    public void initIocCache(Map<String, MyBeanDefinition> beanDefinitionMap, Environment environment) throws Exception {
        this.environment = environment;
        List<MyBeanDefinition> sortedDefinitions = new ArrayList<>(beanDefinitionMap.values());
        sortedDefinitions.sort((def1, def2) -> {
            boolean isBP1 = BeanPostProcessor.class.isAssignableFrom(def1.getBeanClass()) || InstantiationAwareBeanPostProcessor.class.isAssignableFrom(def1.getBeanClass());
            boolean isBP2 = BeanPostProcessor.class.isAssignableFrom(def2.getBeanClass()) || InstantiationAwareBeanPostProcessor.class.isAssignableFrom(def2.getBeanClass());
            boolean isAspect1 = def1.getBeanClass().isAnnotationPresent(MyAspect.class);
            boolean isAspect2 = def2.getBeanClass().isAnnotationPresent(MyAspect.class);
            if (isBP1 && !isBP2) {
                return -1;
            } else if (!isBP1 && isBP2) {
                return 1;
            } else if (!isBP1 && !isBP2 && isAspect1 && !isAspect2) {
                return -1;
            } else if (!isBP1 && !isBP2 && !isAspect1 && isAspect2) {
                return 1;
            }
            return 0;
        });
        beanDefinitionMap.clear();
        sortedDefinitions.forEach(def -> beanDefinitionMap.put(def.getName(), def));
        registerBeanDefinition(beanDefinitionMap);
        for (MyBeanDefinition definition : beanDefinitionMap.values()) {
            cycleSet = new ArrayList<>();
            Object bean = initBean(definition);
            //通过之前的排序保证后置处理器优先被init
            if (bean instanceof InstantiationAwareBeanPostProcessor) {
                instantiationAwareProcessors.add((InstantiationAwareBeanPostProcessor) bean);
            } else if (bean instanceof BeanPostProcessor) {
                regularPostProcessors.add((BeanPostProcessor) bean);
            }
            else if (bean.getClass().getAnnotation(MyAspect.class) != null){
                aopFactories.add((AutumnAopFactory) bean);
            }
            if (!cycleSet.isEmpty()) {
                beanDependencies.put(definition.getName(), new ArrayList<>(cycleSet));
            }
            times = 1;
        }
        //xxx: 检查循环依赖
        DependencyChecker dependencyChecker = (DependencyChecker) getBean(DependencyChecker.class.getName());
        dependencyChecker.checkForCyclicDependencies(beanDependencies);
        this.registerShutdownHook();

    }



    private Object initBean(MyBeanDefinition myBeanDefinition) throws Exception {
        //获取bean的实例
        Object bean = getBean(myBeanDefinition.getName());
        if (bean != null) {
            //因为后置处理器的干预,有些bean不需要依赖注入,所以进行询问
            boolean continueWithInstantiation = doInstantiationAwareBeanPostProcessorAfter(bean,myBeanDefinition.getName());
            if (continueWithInstantiation) {
                if (bean instanceof BeanFactoryAware) {
                    //如果实现感知接口就注入BeanFactory给他
                    ((BeanFactoryAware) bean).setBeanFactory(this);
                }
                //xxx:对未成熟bean进行依赖注入
                autowireBeanProperties(bean, myBeanDefinition);
            }
            bean = doBeanPostProcessorsBefore(bean, myBeanDefinition.getName());
            Method initMethod = myBeanDefinition.getInitMethod();
            if (initMethod != null) {
                initMethod.invoke(bean);
            }
            Method  afterMethod=myBeanDefinition.getAfterMethod();
            if (afterMethod != null) {
                afterMethods.add(Map.of(afterMethod,bean));
            }
            bean = doBeanPostProcessorsAfter(bean, myBeanDefinition.getName());
        } else {
            log.error("Bean为空");
        }
        return bean;
    }


    private Object doBeanPostProcessorsAfter(Object bean, String beanName) throws Exception {
        for (BeanPostProcessor processor : regularPostProcessors) {
            Object result = processor.postProcessAfterInitialization(bean, beanName);
            if (result == null) {
                return bean;
            }
            bean = result;
        }
        return bean;
    }

    private Object doBeanPostProcessorsBefore(Object bean, String beanName) throws Exception {
        for (BeanPostProcessor processor : regularPostProcessors) {
            Object result = processor.postProcessBeforeInitialization(bean, beanName);
            if (result == null) {
                return bean;
            }
            //xxx 如果处理器返回null,继续使用当前bean
            bean = result;
        }
        return bean;
    }

    private boolean doInstantiationAwareBeanPostProcessorAfter(Object bean, String beanName) throws Exception {
        for (InstantiationAwareBeanPostProcessor processor : instantiationAwareProcessors) {
            if (!processor.postProcessAfterInstantiation(bean, beanName)) {
                return false;
            }
        }
        return true;
    }

    //xxx:遍历set去填充第三缓存
    private void registerBeanDefinition(Map<String, MyBeanDefinition> beanDefinitionMaps) {
        this.beanDefinitions.putAll(beanDefinitionMaps);
        for (MyBeanDefinition beanDefinition : beanDefinitions.values()) {
            //xxx:先看看用哪个工厂
            ObjectFactory<?> beanFactory = createBeanFactory(beanDefinition);
            //xxx:然后填充到第三级缓存中
            singletonFactories.put(beanDefinition.getName(), beanFactory);
        }
    }


    private Object createAutumnBeanInstance(MyBeanDefinition mb) {
        try {
            Object configInstance = getBean(mb.getConfigurationClass().getName());
            if (configInstance instanceof BeanFactoryAware) {
                ((BeanFactoryAware) configInstance).setBeanFactory(this);
            }
            //xxx:对配置类进行依赖注入,得到成熟的bean
            autowireBeanProperties(configInstance, mb);
            //xxx:获取mb定义的生产方法
            Method beanMethod = mb.getDoMethod();
            return beanMethod.invoke(configInstance);

        } catch (Exception e) {
            log.error(String.valueOf(e));

            throw new BeanCreationException("创建@bean标注的实例失败,请检查你是否存在一个有参构造器,有的话创建一个无参构造器", e);
        }
    }

    //xxx:判断使用哪种工厂
    private ObjectFactory<?> createBeanFactory(MyBeanDefinition mb) {
        //xxx:第三方实现
        if (mb.isStarter()) {
            return () -> createStarterBeanInstance(mb);
        }
        else if (mb.getDoMethod() != null) {
            return () -> createAutumnBeanInstance(mb);
        } else {
            return () -> createBeanInstance(mb.getBeanClass());
        }

    }


private Object doInstantiationAwareBeanPostProcessorBefore(String beanName, ObjectFactory<?> factory) throws Exception {
    Object currentResult = null;
    Class<?> beanClass = beanDefinitions.get(beanName).getBeanClass();
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




    private Object createStarterBeanInstance(MyBeanDefinition mb) {
        try {
            ObjectFactory<?> factory = mb.getStarterMethod();
            if (factory == null) {
                throw new IllegalStateException("没有为 " + mb.getBeanClass().getName() + " 定义工厂方法");
            }
            Object beanInstance = factory.getObject();

            if (mb.getInitMethod() != null) {
                mb.getInitMethod().invoke(beanInstance);
            }
            return beanInstance;

        } catch (Exception e) {
            log.error("创建Bean实例失败: {}", mb.getName(), e);
            throw new BeanCreationException("创建Bean实例失败: " + mb.getName(), e);
        }
    }


    //xxx:普通bean工厂
    private Object createBeanInstance(Class<?> beanClass) {
        try {
            Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.isAnnotationPresent(MyAutoWired.class)) {
                    Object[] parameters = Arrays.stream(constructor.getParameters())
                            .map(param -> getBean(param.getType().getName()))
                            .toArray(Object[]::new);
                    return constructor.newInstance(parameters);

                }
            }

            return beanClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new BeanCreationException("创建普通bean实例失败,请检查你是否存在一个有参构造器,有的话创建一个无参构造器", e);
        }
    }

    //xxx:di部分:
    private void autowireBeanProperties(Object bean, MyBeanDefinition mb) throws IllegalAccessException, NoSuchFieldException {
        //xxx:是否为CgLib代理类?,是的话因为子类不能继承父类字段等注解,需要去父类身上查找
        Class<?> clazz = bean.getClass().getName().contains("$$")
                ? bean.getClass().getSuperclass() : bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            //xxx:标记@auto wired进行对象/接口依赖注入
            if (field.isAnnotationPresent(MyAutoWired.class)) {
                String myAutoWired = field.getAnnotation(MyAutoWired.class).value();
                if(field.getType().equals(AutumnRequest.class)){
                    field.setAccessible(true);
                    field.set(bean, AutumnRequestProxyFactory.createAutumnRequestProxy());
                    continue;
                }
                if(field.getType().equals(AutumnResponse.class)){
                    field.setAccessible(true);
                    field.set(bean, AutumnRequestProxyFactory.createAutumnResponseProxy());
                    continue;
                }
                if (field.isAnnotationPresent(Lazy.class)){
                    field.setAccessible(true);
                    field.set(bean, LazyBeanFactory.createLazyBeanProxy(field.getType(),()->{
                        log.info("延迟懒加载触发,现在开始获取对象");
                        return getBean(field.getType().getName());
                    }));
                    continue;
                }

                if (myAutoWired.isEmpty()) {
                    log.warn("开始依赖注入,被处理的类是{}处理的字段是{}", bean.getClass().getSimpleName(), field.getName());
                    injectDependencies(bean, field, mb);
                } else {
                    Object dependency = getBean(myAutoWired);
                    if (dependency == null) {
                        log.warn("无法解析的依赖：{}", myAutoWired);
                        return;
                    }
                    field.setAccessible(true);
                    field.set(bean, dependency);
                }

            }
            //xxx:进行配置文件注入
            else if (field.isAnnotationPresent(Value.class)) {
                injectValueAnnotation(bean, field);
            }

        }
        //xxx:依赖注入后更新缓存,这个bean从第二缓存移除,进入一级缓存,提前暴露期转为成熟的AutumnBean

        mb.setInstance(bean);
        singletonObjects.put(bean.getClass().getName(), bean);
        earlySingletonObjects.remove(bean.getClass().getName());
    }

    private void injectDependencies(Object bean, Field field, MyBeanDefinition mb) throws IllegalAccessException, NoSuchFieldException {
        Class<?> fieldType = field.getType();
        //xxx:接口,那一定是service或者神秘力量拉进来的
        if (fieldType.isInterface()) {
            Object dependency = getBean(fieldType.getName());
            if (dependency == null) {
                //xxx:如果容器中没有实例,进入查找实现类或第三方组件处理环节
                injectInterfaceTypeDependency(bean, field, mb);

            } else {
                //xxx:如果容器中存在实例,直接注入
                field.setAccessible(true);
                field.set(bean, dependency);
            }
        } else {
            //xxx:正常的Bean
            injectNormalDependency(bean, field);
        }
    }



    //xxx:这个是依照接口找实现类
    private void injectInterfaceTypeDependency(Object bean, Field field, MyBeanDefinition mb) throws IllegalAccessException, NoSuchFieldException {
        String packageName = (String) get("packageUrl");
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        Set<Class<?>> subTypesOf = new HashSet<>(reflections.getSubTypesOf(field.getType()));
        Set<Class<?>> classesToRemove = new HashSet<>();
        for (Class<?> implClass : subTypesOf) {
            MyConditional myConditionalAnnotation = implClass.getAnnotation(MyConditional.class);
            if (myConditionalAnnotation != null) {
                Class<? extends MyCondition> conditionClass = myConditionalAnnotation.value();
                MyCondition condition = (MyCondition) getBean(conditionClass.getName());
                autowireBeanProperties(condition, mb);
                condition.init();
                if (!condition.matches(this, implClass)) {
                    classesToRemove.add(implClass);
                }
                condition.after();
            }
        }
        subTypesOf.removeAll(classesToRemove);
        Class<?> selectedImpl = null;
        if (subTypesOf.size() == 1) {
            selectedImpl = subTypesOf.iterator().next();
        } else if (subTypesOf.size() > 1) {
            log.error("多个实现类均命中,请添加合理的条件注解来进行选择性注入,冲突的实现类如下:\n{}", subTypesOf);
            throw new BeanCreationException("多个实现类命中");
        }
        if (selectedImpl != null) {
            Object dependency = getBean(selectedImpl.getName());
            field.setAccessible(true);
            field.set(bean, dependency);
        } else {
            throw new BeanCreationException("无法解析的依赖：" + field.getType());
        }
    }


    //xxx:注入一般Bean,依照字段查找类,从容器取出为字段赋值
    private void injectNormalDependency(Object bean, Field field) throws IllegalAccessException {
        Class<?> dependencyType = field.getType();
        //xxx:去容器里看看有没有啊,如果循环依赖了?没关系,反正容器里都是单例,注入一个未成熟的bean进去也无所谓,反正内存地址都一样,结束的时候你缺少我,我缺少你,除此之外都完整,所以我们合到一起就是完整的
        Object dependency = getBean(dependencyType.getName());
        if (dependency == null) {
            log.warn("无法解析的依赖：{}", dependencyType.getName());
            return;
        }
        field.setAccessible(true);
        field.set(bean, dependency);

    }



    //xxx:注入配置文件
    private void injectValueAnnotation(Object instance, Field field) {
        Value value = field.getAnnotation(Value.class);
        if (value == null || "".equals(value.value())) {
            log.error("没有传递内容,注入失败");
            return;
        }

        String propertyValue = properties.getProperty(value.value());
        if (propertyValue == null) {
            log.error("属性未找到,注入失败");
            return;
        }

        try {
            field.setAccessible(true);
            Object convertedValue = propertiesReader.convertStringToType(propertyValue, field.getType());
            field.set(instance, convertedValue);
        } catch (Exception e) {
            log.error("依赖注入失败：{}", e.getMessage());
        }
    }

    @Override
    public void put(String key, Object value) {
        sharedMap.put(key, value);
    }

    @Override
    public Object get(String key) {
        return sharedMap.get(key);
    }


    @Override
    public Map<String, Object> getIocContainer() {
        return singletonObjects;
    }

    @Override
    public Properties getProperties(){
        return properties;
    }

    @Override
    public List<Object> getBeansByAnnotation(Class<? extends Annotation> annotationClass) {
        List<Object> beansWithAnnotation = new ArrayList<>();
        for (Object bean : singletonObjects.values()) {
            Class<?> beanClass = bean.getClass();
            // 检查是否为 CGLIB 代理类
            if (beanClass.getName().contains("$$")) {
                beanClass = beanClass.getSuperclass();
            }
            if (beanClass.isAnnotationPresent(annotationClass)) {
                beansWithAnnotation.add(bean);
            }
        }
        return beansWithAnnotation;
    }

    @Override
    public void addBean(String name, Object bean) {
        singletonObjects.put(name, bean);
    }

    @Override
    public <T> List<T> getBeansOfType(Class<T> type) {
        List<T> beans = new ArrayList<>();
        for (Object bean : singletonObjects.values()) {
            if (type.isInstance(bean)) {
                beans.add(type.cast(bean));
            }
        }
        return beans;
    }

    @Override
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.warn("进行关机清理....请稍后");
            for (Map<Method, Object> afterMethod : afterMethods) {
                for (Map.Entry<Method, Object> entry : afterMethod.entrySet()) {
                    try {
                        entry.getKey().invoke(entry.getValue());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error("调用销毁方法失败", e);
                    }
                }
            }
            log.warn("已成功停机");
        }));
    }

    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public void refresh() {
        try {
            initIocCache(beanDefinitions, environment);
        } catch (Exception e) {
            log.error("初始化IOC容器失败", e);
        }
    }


}
