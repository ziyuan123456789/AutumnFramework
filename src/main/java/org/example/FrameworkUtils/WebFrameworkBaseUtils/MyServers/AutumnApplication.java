package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.EnableAutoConfiguration;
import org.example.FrameworkUtils.AutumnCore.Annotation.Import;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAspect;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPostConstruct;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPreDestroy;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyService;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AutumnFactoriesLoader;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.XMLBeansLoader;
import org.example.FrameworkUtils.AutumnCore.Bootstrap.BootstrapRegistryInitializer;
import org.example.FrameworkUtils.AutumnCore.Bootstrap.DefaultBootstrapContext;
import org.example.FrameworkUtils.AutumnCore.Event.IocInitEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationListener;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.AutumnApplicationRunListener;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.EventListener;
import org.example.FrameworkUtils.AutumnCore.Event.Publisher.EventMulticaster;
import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnBeanFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanDefinitionRegistry;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanDefinitionRegistryPostProcessor;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryPostProcessor;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanPostProcessor;
import org.example.FrameworkUtils.AutumnCore.Ioc.FactoryBean;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.Ordered;
import org.example.FrameworkUtils.AutumnCore.Ioc.PriorityOrdered;
import org.example.FrameworkUtils.AutumnCore.Ioc.SimpleMyBeanDefinitionRegistry;
import org.example.FrameworkUtils.AutumnCore.context.ApplicationContextInitializer;
import org.example.FrameworkUtils.AutumnCore.env.ApplicationArguments;

import org.example.FrameworkUtils.AutumnCore.env.DefaultApplicationArguments;
import org.example.FrameworkUtils.AutumnCore.env.DefaultEnvironment;
import org.example.FrameworkUtils.AutumnCore.env.Environment;
import org.example.FrameworkUtils.Exception.BeanCreationException;
import org.example.FrameworkUtils.Utils.AnnotationUtils;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.WebSocket.MyWebSocketConfig;



import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ziyuan
 * @since 2023.10
 */

/*
 * 核心启动类,当时在设计这个类的时候糅合了网络与容器部分,正常来说这个启动类应该只负责容器的创建,其他部分用插件形式来进行扩展
 * 不过随着条件注解,复合注解,简易spi机制,与各种后置处理器的加入形成了简单的自动装配机制,启动类的作用就逐渐降低了
 * 今后会从启动类完整剔除网络部分
 */
@Slf4j
public class AutumnApplication {

    private Class<?> primarySources;

    private List<BootstrapRegistryInitializer> bootstrapRegistryInitializers;

    private List<ApplicationContextInitializer> initializers;

    private List<ApplicationListener> listeners;


    private Map<String, List<String>> spiMap;

    private Map<String, List<String>> autoConfigurationMap;

    private String[] sysArgs;

    AutumnBeanFactory beanFactory ;

    AnnotationScanner scanner = new AnnotationScanner();


    public AutumnApplication(Class<?> mainClass) {
        Assert.notNull(mainClass, "主类不得为空");
        this.primarySources = mainClass;
        this.initAutumnSpi();
        this.bootstrapRegistryInitializers = this.getAutumnFactoriesInstances(BootstrapRegistryInitializer.class);
        this.setInitializers(this.getAutumnFactoriesInstances(ApplicationContextInitializer.class));
        this.setListeners(this.getAutumnFactoriesInstances(ApplicationListener.class));
    }

    private void initAutumnSpi() {
        try {
            spiMap = AutumnFactoriesLoader.parseConfigurations();
            autoConfigurationMap=AutumnFactoriesLoader.parseAutoConfigurations();
        } catch (Exception e) {
            log.error("加载spi配置文件失败", e);
            throw new RuntimeException(e);
        }
    }

    public void setListeners(List<ApplicationListener> listeners) {
        this.listeners = new ArrayList<>(listeners);
    }

    public void setInitializers(List<? extends ApplicationContextInitializer> initializers) {
        this.initializers = new ArrayList<>(initializers);
    }

    public void addApplicationContextInitializer(ApplicationContextInitializer initializer) {
        this.initializers.add(initializer);
    }

    public void addApplicationListener(ApplicationListener listener) {
        this.listeners.add(listener);
    }


    public void addInitializers(BootstrapRegistryInitializer initializer) {
        this.bootstrapRegistryInitializers.add(initializer);
    }



    private <T> List<T> getAutumnFactoriesInstances(Class<T> type) {
        try{
            List<String> initializers = new ArrayList<>(spiMap.get(type.getSimpleName()));
            List<T> result = new ArrayList<>();
            for (String className : initializers) {

                    Class<?> clazz = Class.forName(className);
                    if (type.isAssignableFrom(clazz)) {
                        result.add((T) clazz.getDeclaredConstructor().newInstance());
                    }

            }
            return result;
        }catch (Exception e){
            log.warn("装载失败");
            return new ArrayList<>();
        }


    }

    public void run(String[] args) {
        this.sysArgs=args;
        DefaultBootstrapContext bootstrapContext = this.createBootstrapContext();
        List<AutumnApplicationRunListener> listeners = getAutumnFactoriesInstances(AutumnApplicationRunListener.class);
        for (AutumnApplicationRunListener listener : listeners) {
            listener.starting(bootstrapContext, primarySources);
        }
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
        Environment environment = prepareEnvironment(listeners, bootstrapContext, applicationArguments);


        try {
            //保证容器已经存在
            Class<?> clazz = Class.forName("org.example.FrameworkUtils.AutumnCore.Ioc.MyContext");
            Method getInstanceMethod = clazz.getDeclaredMethod("getInstance");
            getInstanceMethod.setAccessible(true);
            beanFactory = (AutumnBeanFactory) getInstanceMethod.invoke(null);
        } catch(Exception e){
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        log.info("""

                \033[35m                                _                         __  ____      _______\s
                \033[36m                     /\\        | |                       |  \\/  \\ \\    / / ____|
                \033[32m                    /  \\  _   _| |_ _   _ _ __ ___  _ __ | \\  / |\\ \\  / / |    \s
                \033[34m                   / /\\ \\| | | | __| | | | '_ ` _ \\| '_ \\| |\\/| | \\ \\/ /| |    \s
                \033[33m                  / ____ \\ |_| | |_| |_| | | | | | | | | | |  | |  \\  / | |____\s
                \033[31m                 /_/    \\_\\__,_|\\__|\\__,_|_| |_| |_|_| |_|_|  |_|   \\/   \\_____|\033[0m"""
        );

        //xxx:保存主类的包名,也就是默认扫描的包,以后会开放自定义扫包路径
        beanFactory.put("packageUrl", primarySources.getPackageName());
        try {
            componentScan(primarySources, beanFactory);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        //这一步是在配置路由表
        Map<String, String> urlMap = new ConcurrentHashMap<>();
        Map<String, Object> iocContainer = beanFactory.getIocContainer();
        //有些类在aop后置处理器会被替换,所以要检查是不是cglib代理类,是的话要去看他的父类也就是原生类,要不然扫描不到注解,因为注解不会继承
        for (String clazz : iocContainer.keySet()) {
            try{
                Class<?> temp = Class.forName(clazz);
                if (temp.getName().contains("$$EnhancerByCGLIB")) {
                    processClassForMapping(temp.getSuperclass(),urlMap);
                } else {
                    processClassForMapping(temp,urlMap);
                }
            } catch (Exception ignored) {

            }

        }
        beanFactory.put("urlmapping", urlMap);
        //运行时判断环境,依照自定义的条件注解选择运行容器,有我写的socketserver和tomcat
        ServerRunner server = (ServerRunner) beanFactory.getBean(ServerRunner.class.getName());
        server.run();
    }

    private Environment prepareEnvironment(List<AutumnApplicationRunListener> listeners, DefaultBootstrapContext bootstrapContext, ApplicationArguments applicationArguments) {
        Environment environment = new DefaultEnvironment(applicationArguments);
        environment.loadConfiguration("src/main/resources/test.properties");
        for (AutumnApplicationRunListener listener : listeners) {
            listener.environmentPrepared(environment);
        }
        bootstrapContext.setEnvironment(environment);
        return environment;

    }

    private DefaultBootstrapContext createBootstrapContext() {
        DefaultBootstrapContext bootstrapContext = new DefaultBootstrapContext();
        this.bootstrapRegistryInitializers.forEach((initializer) -> initializer.initialize(bootstrapContext));
        return bootstrapContext;
    }


    private void componentScan(Class<?> mainClass, AutumnBeanFactory myContext) throws Exception {
        //xml方式加载beans,弃用
        XMLBeansLoader xmlBeansLoader = new XMLBeansLoader();
        SimpleMyBeanDefinitionRegistry registry = new SimpleMyBeanDefinitionRegistry();
        List<Class<? extends Annotation>> annotations = new ArrayList<>();
        annotations.add(MyController.class);
        annotations.add(MyService.class);
        annotations.add(MyComponent.class);
        annotations.add(MyConfig.class);
        annotations.add(MyWebSocketConfig.class);
        annotations.add(MyAspect.class);
        Set<Class<?>> annotatedClasses = scanner.findAnnotatedClassesList(mainClass.getPackageName(), annotations);
        //首先我们检测main方法的包下符合注解要求的类
        List<Class<BeanFactoryPostProcessor>> beanFactoryPostProcessorsClassList = new ArrayList<>();
        //现在开始魔法时间,引入starter,实际上注解本身没有意义,其实应该是复合注解import一个大爹来调度bean的装配
        if (!AnnotationUtils.findAllClassAnnotations(mainClass, EnableAutoConfiguration.class).isEmpty()) {
//        if (mainClass.getAnnotation(EnableAutoConfiguration.class) != null) {
            // 加载自动配置类

            List<String> allProcessors = new ArrayList<>(autoConfigurationMap.get("BeanDefinitionRegistryPostProcessor"));
            allProcessors.addAll(autoConfigurationMap.get("BeanFactoryPostProcessor"));

            // 处理所有处理器类，并且检查它们是否是 BeanFactoryPostProcessor 类型的子类
            for (String className : allProcessors) {
                try {
                    Class<?> clazz = Class.forName(className);
                    processImports(annotatedClasses, clazz, beanFactoryPostProcessorsClassList);
                    // 如果是 BeanFactoryPostProcessor，添加到处理器列表
                    if (BeanFactoryPostProcessor.class.isAssignableFrom(clazz)) {
                        beanFactoryPostProcessorsClassList.add((Class<BeanFactoryPostProcessor>) clazz);
                    }
                } catch (ClassNotFoundException e) {
                    log.warn(e.getMessage(), e);
                }
            }

            //处理所有通过配置文件指定的 Bean类
            for (String className : spiMap.get("Beans")) {
                try {
                    Class<?> clazz = Class.forName(className);
                    annotatedClasses.add(clazz);
                    processImports(annotatedClasses, clazz, beanFactoryPostProcessorsClassList);
                } catch (ClassNotFoundException e) {
                    System.err.println("未找到类: " + className);
                }
            }
        }

        //递归处理所有通过@Import注解引入的类
        for (Class<?> clazz : annotatedClasses.toArray(new Class[0])) {
            processImports(annotatedClasses, clazz, beanFactoryPostProcessorsClassList);
        }


//        List<Class<BeanFactoryPostProcessor>> starterRegisterer = xmlBeansLoader.loadStarterClasses("plugins");
        long startTime = System.currentTimeMillis();
        log.info("IOC容器开始初始化");
        for (Class<?> clazz : annotatedClasses) {
            List<MyConfig> allClassAnnotations = AnnotationUtils.findAllClassAnnotations(clazz, MyConfig.class);
//            MyConfig myConfig = clazz.getAnnotation(MyConfig.class);
            if (!allClassAnnotations.isEmpty() || FactoryBean.class.isAssignableFrom(clazz)) {
                if (clazz.isAnnotation()) {
                    continue;
                }
                MyBeanDefinition myConfigBeanDefinition = new MyBeanDefinition();
                myConfigBeanDefinition.setName(clazz.getName());
                myConfigBeanDefinition.setBeanClass(clazz);
                registry.registerBeanDefinition(myConfigBeanDefinition.getName(), myConfigBeanDefinition);
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    getInitOrAfterMethod(myConfigBeanDefinition, method);
                    if (method.getAnnotation(AutumnBean.class) != null) {
                        //xxx: 当这个方法有bean注解则创建一个MyBeanDefinition
                        MyBeanDefinition myBeanDefinition = getMyBeanDefinition(clazz, method);
                        registry.registerBeanDefinition(myBeanDefinition.getName(), myBeanDefinition);
                    }

                }
                //实际上这是一个完全错误的设计,但确实可以起到作用
                if (FactoryBean.class.isAssignableFrom(clazz)) {
                    try {
                        Method getObjectMethod = clazz.getMethod("getObject");
                        MyBeanDefinition myBeanDefinition = getMyBeanDefinition(clazz, getObjectMethod);
                        registry.registerBeanDefinition(myBeanDefinition.getName(), myBeanDefinition);
                    } catch (NoSuchMethodException e) {
                        System.err.println("FactoryBean实现中没有找到getObject方法: " + clazz.getName());
                    }
                }
            } else {
                if (clazz.isAnnotation()) {
                    continue;
                }
                MyBeanDefinition myBeanDefinition = new MyBeanDefinition();
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    getInitOrAfterMethod(myBeanDefinition, method);
                }
                myBeanDefinition.setName(clazz.getName());
                myBeanDefinition.setBeanClass(clazz);
                registry.registerBeanDefinition(myBeanDefinition.getName(), myBeanDefinition);
            }
        }

        //xxx:  区分处理BeanDefinitionRegistryPostProcessor和BeanFactoryPostProcessor
        List<BeanDefinitionRegistryPostProcessor> registryPostProcessors = new ArrayList<>();
        List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();

        for (Class<BeanFactoryPostProcessor> postProcessorClass : beanFactoryPostProcessorsClassList) {
            //xxx:  构造器创建一个实例
            BeanFactoryPostProcessor postProcessor = postProcessorClass.getDeclaredConstructor().newInstance();
            if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                //xxx:  是BeanDefinitionRegistryPostProcessor接口的,放一边
                registryPostProcessors.add((BeanDefinitionRegistryPostProcessor) postProcessor);
            } else {
                //xxx:  是BeanFactoryPostProcessor接口的,放另一边
                regularPostProcessors.add(postProcessor);
            }
        }

        //xxx:  处理BeanDefinitionRegistryPostProcessor
        invokePostProcessors(registryPostProcessors, registry);

        //xxx:  处理BeanFactoryPostProcessor
        invokePostProcessors(regularPostProcessors, registry);
        for(MyBeanDefinition mb:registry.getBeanDefinitionMap().values()){

            if(mb.getBeanClass().isAssignableFrom(BeanPostProcessor.class)){

            }
        }
        MyContext myContext1 = (MyContext) myContext;
        myContext1.initIocCache(registry.getBeanDefinitionMap());
        long endTime = System.currentTimeMillis();
        log.info("容器花费了：{} 毫秒实例化", endTime - startTime);
        Map<String, Object> iocContainer = myContext1.getIocContainer();
        EventMulticaster eventMulticaster = (EventMulticaster) iocContainer.get(EventMulticaster.class.getName());
        for (Map.Entry<String, Object> entry : iocContainer.entrySet()) {
            Object bean = entry.getValue();
            if (bean instanceof EventListener && !bean.getClass().isInterface()) {
                eventMulticaster.addEventListener((EventListener<?>) bean);
                log.warn("已注册监听器: {}", bean.getClass().getName());
            }
        }
        IocInitEvent event = new IocInitEvent("容器初始化结束", endTime - startTime);
        eventMulticaster.publishEvent(event);
    }

    private void processImports(Set<Class<?>> annotatedClasses, Class<?> clazz, List<Class<BeanFactoryPostProcessor>> beanFactoryPostProcessorsClassList) {
        List<Import> importAnnotations = AnnotationUtils.findAllClassAnnotations(clazz, Import.class);

        if (!importAnnotations.isEmpty()) {
            for (Import importAnnotation : importAnnotations) {
                for (Class<?> importClass : importAnnotation.value()) {
                    if (importClass.isInterface() || importClass.isAnnotation()) {
                        throw new RuntimeException("Import注解不能引入接口或注解: " + importClass.getName());
                    }
                    log.warn("通过Import机制导入了{}", importClass.getName());
                    annotatedClasses.add(importClass);

                    if (BeanFactoryPostProcessor.class.isAssignableFrom(importClass)) {
                        beanFactoryPostProcessorsClassList.add((Class<BeanFactoryPostProcessor>) importClass);
                    }

                    processImports(annotatedClasses, importClass, beanFactoryPostProcessorsClassList);
                }
            }
        }
    }

    private void getInitOrAfterMethod(MyBeanDefinition myBeanDefinition, Method method) {
        if(method.getAnnotation(MyPostConstruct.class)!=null){
            myBeanDefinition.setInitMethodName(method.getName());
            myBeanDefinition.setInitMethod(method);
        }
        if(method.getAnnotation(MyPreDestroy.class)!=null){
            myBeanDefinition.setAfterMethodName(method.getName());
            myBeanDefinition.setAfterMethod(method);
        }
    }

    private void invokePostProcessors(List<?> postProcessors, SimpleMyBeanDefinitionRegistry registry) throws Exception {
        //xxx:  处理实现了PriorityOrdered接口的
        List<Object> priorityOrderedPostProcessors = new ArrayList<>();
        for (Object postProcessor : postProcessors) {
            if (postProcessor instanceof PriorityOrdered) {
                priorityOrderedPostProcessors.add(postProcessor);
            }
        }
        for (Object postProcessor : priorityOrderedPostProcessors) {
            invokePostProcessor(postProcessor, registry);
        }
        postProcessors.removeAll(priorityOrderedPostProcessors);

        //xxx:  处理实现了Ordered接口的
        List<Object> orderedPostProcessors = new ArrayList<>();
        for (Object postProcessor : postProcessors) {
            if (postProcessor instanceof Ordered) {
                orderedPostProcessors.add(postProcessor);
            }
        }
        for (Object postProcessor : orderedPostProcessors) {
            invokePostProcessor(postProcessor, registry);
        }
        postProcessors.removeAll(orderedPostProcessors);

        //xxx:  处理其他所有的
        for (Object postProcessor : postProcessors) {
            invokePostProcessor(postProcessor, registry);
        }
    }

    private void invokePostProcessor(Object postProcessor, BeanDefinitionRegistry registry) throws Exception {
        if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
            ((BeanDefinitionRegistryPostProcessor) postProcessor).postProcessBeanDefinitionRegistry(scanner, registry);
        } else if (postProcessor instanceof BeanFactoryPostProcessor) {
            ((BeanFactoryPostProcessor) postProcessor).postProcessBeanFactory(scanner, registry);
        }
    }


    private static MyBeanDefinition getMyBeanDefinition(Class clazz, Method method) {
        Class<?> returnType = method.getReturnType();
        if (returnType.equals(void.class)) {
            throw new BeanCreationException("用AutumnBean注解标记的方法返回值不能为void");
        }
        MyBeanDefinition myBeanDefinition = new MyBeanDefinition();
        myBeanDefinition.setDoMethod(method);
        myBeanDefinition.setConfigurationClass(clazz);
        for (Method returnTypeMethod : returnType.getDeclaredMethods()) {
            if (returnTypeMethod.isAnnotationPresent(MyPostConstruct.class)) {
                myBeanDefinition.setInitMethodName(returnTypeMethod.getName());
                myBeanDefinition.setInitMethod(returnTypeMethod);
            }
            if (returnTypeMethod.isAnnotationPresent(MyPreDestroy.class)) {
                myBeanDefinition.setAfterMethodName(returnTypeMethod.getName());
                myBeanDefinition.setAfterMethod(returnTypeMethod);
            }
        }
        AutumnBean annotation = method.getAnnotation(AutumnBean.class);
        if (annotation == null && FactoryBean.class.isAssignableFrom(clazz) ) {
            myBeanDefinition.setName(returnType.getName());
            myBeanDefinition.setBeanClass(returnType);
            return myBeanDefinition;

        } else {
            if (method.getAnnotation(AutumnBean.class).value().isEmpty()) {
                myBeanDefinition.setName(returnType.getName());
            } else {
                myBeanDefinition.setName(method.getAnnotation(AutumnBean.class).value());
            }
            myBeanDefinition.setBeanClass(returnType);
            return myBeanDefinition;
        }

    }

    private void processClassForMapping(Class<?> clazz, Map<String, String> urlMap) {
        if (clazz.isAnnotationPresent(MyController.class)) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(MyRequestMapping.class)) {
                    MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
                    String value = annotation.value();
                    log.info("映射URL: {}", value);
                    if (!value.isEmpty()) {
                        String fullMethodName = clazz.getName() + "." + method.getName();
                        urlMap.put(value, fullMethodName);
                    }
                }
            }
        }
    }

}
