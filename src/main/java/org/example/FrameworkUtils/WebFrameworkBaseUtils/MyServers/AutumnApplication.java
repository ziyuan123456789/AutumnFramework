package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPostConstruct;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPreDestroy;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AutumnFactoriesLoader;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.BeanDefinitionLoader;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.Bootstrap.BootstrapRegistryInitializer;
import org.example.FrameworkUtils.AutumnCore.Bootstrap.DefaultBootstrapContext;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationListener;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.AutumnApplicationRunListener;
import org.example.FrameworkUtils.AutumnCore.Ioc.AnnotationConfigApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.AnnotationConfigServletWebServerApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationShutdownHook;
import org.example.FrameworkUtils.AutumnCore.Ioc.FactoryBean;
import org.example.FrameworkUtils.AutumnCore.context.ApplicationContextInitializer;
import org.example.FrameworkUtils.AutumnCore.env.ApplicationArguments;
import org.example.FrameworkUtils.AutumnCore.env.ConfigurableEnvironment;
import org.example.FrameworkUtils.AutumnCore.env.DefaultApplicationArguments;
import org.example.FrameworkUtils.AutumnCore.env.DefaultEnvironment;
import org.example.FrameworkUtils.Exception.BeanCreationException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author ziyuan
 * @since 2023.10
 */

/*
 * 核心启动类
 */
@Slf4j
public class AutumnApplication {

    private final Set<Class<?>> primarySources;

    private final AnnotationScanner scanner = new AnnotationScanner();

    private final List<BootstrapRegistryInitializer> bootstrapRegistryInitializers;

    private final Class<?> mainApplicationClass;

    private List<ApplicationContextInitializer> initializers;

    private List<ApplicationListener> listeners;

    private Map<String, List<String>> spiMap;

    private Map<String, List<String>> autoConfigurationMap;

    private String[] sysArgs;

    private ApplicationContext beanFactory;

    private ConfigurableEnvironment environment;

    private static final ApplicationShutdownHook shutdownHook = new ApplicationShutdownHook();


    public AutumnApplication(Class<?>... primarySources) {
        /**
         * 非常感谢你能看到这里,能看到这段注释也说明你克隆了这段代码并且真的点进去看了,autumnFramework仅仅是对springboot的基础结构和基础功能进行简单的仿写
         * 对于springboot这样的庞然大物想要去梳理结构甚至看到细枝末节都是一件极其不易的事情
         * 作为一个称霸整个javaWeb生态的框架肩负了太多历史包袱,控制反转和面对接口编程在spring的源码中体现的淋漓尽致
         * 对于我们这样的学习者来说无疑更是增加了不少困难,在阅读,整理,理解的过程中我也买了不少的书,翻遍了各种博客也问遍了各种ai
         * 我能力有限,工作经验也并不丰富,这个项目我从大三下实习开始写起,到今天也有一年半的时间了,一转眼毕业上班也有半年了
         * 在编写项目的过程中还是止不住惊叹于spring生态的强大和复杂,在debug地狱中一层层的翻找,在无数个实现类和接口间闪转腾挪真的非常痛苦
         * 特别在设计AnnotationConfigApplicationContext,AutowiredAnnotationBeanPostProcessor,AnnotationAwareAspectJAutoProxyCreator,ConfigurationClassPostProcessor的时候简直抓耳挠腮,跟着博客打断点一点点观察运行情况
         * 这个bean从哪来的?这怎么就加载了?我的bean定义是被谁加载的?为什么BeanDefinitionRegistryPostProcessor还可以进行getBean操作?这个bean是怎么被代理的?怎么就被代理了
         * 经常几晚上的不出结果,或者出了结果又是运行异常.不过我依然觉得这是一件非常有意思的事,充满了挑战性
         *
         * 但一切都不是完美无缺的,我也经常在思考,是不是java这门历史包袱太重的语言到了今天已经充满了缺陷,从而需要springboot这样的框架来增强语言的能力
         * 不管事实是这样也好,还是仅仅是我的暴论也好,我都会通过注释写下我的真实写法,如果你也有一样的想法/不一致的想法 也欢迎分享出来
         */
        this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
        this.initAutumnSpi();
        this.bootstrapRegistryInitializers = this.getAutumnFactoriesInstances(BootstrapRegistryInitializer.class);
        this.setInitializers(this.getAutumnFactoriesInstances(ApplicationContextInitializer.class));
        this.setListeners(this.getAutumnFactoriesInstances(ApplicationListener.class));
        this.mainApplicationClass = deduceMainApplicationClass();
        this.checkEnv();
    }


    private void prepareContext(DefaultBootstrapContext bootstrapContext, ApplicationContext context, ConfigurableEnvironment environment, List<AutumnApplicationRunListener> listeners, ApplicationArguments applicationArguments) {
        context.setEnvironment(environment);

        for (ApplicationContextInitializer initializer : initializers) {
            initializer.initialize(context);
        }
        for (AutumnApplicationRunListener listener : listeners) {
            listener.contextPrepared(context);
        }

        log.info("""
                
                \033[35m                                _                         __  ____      _______\s
                \033[36m                     /\\        | |                       |  \\/  \\ \\    / / ____|
                \033[32m                    /  \\  _   _| |_ _   _ _ __ ___  _ __ | \\  / |\\ \\  / / |    \s
                \033[34m                   / /\\ \\| | | | __| | | | '_ ` _ \\| '_ \\| |\\/| | \\ \\/ /| |    \s
                \033[33m                  / ____ \\ |_| | |_| |_| | | | | | | | | | |  | |  \\  / | |____\s
                \033[31m                 /_/    \\_\\__,_|\\__|\\__,_|_| |_| |_|_| |_|_|  |_|   \\/   \\_____|\033[0m"""
        );
        beanFactory.registerSingleton("applicationArguments", applicationArguments);
        beanFactory.registerSingleton("environment", environment);
        Set<Object> sources = getAllSources();
        load(context, sources.toArray(new Object[0]));
        for (AutumnApplicationRunListener listener : listeners) {
            listener.contextLoaded(context);
        }


    }

    private ApplicationContext createApplicationContext() {

        if (ApplicationContext.BASE_CONTEXT.equals(environment.getProperty("autumn.beanFactory"))) {
            return new AnnotationConfigServletWebServerApplicationContext();
        } else {
            try {
                Method getInstanceMethod = Class.forName("org.example.FrameworkUtils.AutumnCore.Ioc.MyContext").getDeclaredMethod("getInstance");
                getInstanceMethod.setAccessible(true);
                return (ApplicationContext) getInstanceMethod.invoke(null);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private ConfigurableEnvironment prepareEnvironment(List<AutumnApplicationRunListener> listeners, DefaultBootstrapContext bootstrapContext, ApplicationArguments applicationArguments) {
        DefaultEnvironment environment = new DefaultEnvironment(applicationArguments);
        environment.loadConfiguration("src/main/resources/test.properties", ConfigurableEnvironment.DEFAULT_PROFILE);
        environment.getAllProperties().put("autumn.main.sources", mainApplicationClass.getName());
        environment.getAllProperties().put("autumn.main.package", mainApplicationClass.getPackageName());
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

    private void refreshContext(AnnotationConfigApplicationContext context) {
        shutdownHook.registerApplicationContext(context);
        beanFactory.refresh();

    }

    public void run(String[] args) {
        this.sysArgs = args;
        DefaultBootstrapContext bootstrapContext = this.createBootstrapContext();
        List<AutumnApplicationRunListener> listeners = getAutumnFactoriesInstances(AutumnApplicationRunListener.class);
        for (AutumnApplicationRunListener listener : listeners) {
            listener.starting(bootstrapContext, mainApplicationClass);
        }
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
        environment = prepareEnvironment(listeners, bootstrapContext, applicationArguments);


        beanFactory = createApplicationContext();
        prepareContext(bootstrapContext, beanFactory, environment, listeners, applicationArguments);

        refreshContext((AnnotationConfigApplicationContext) beanFactory);




//        //:保存主类的包名,也就是默认扫描的包,以后会开放自定义扫包路径
//        beanFactory.put("packageUrl", mainApplicationClass.getPackageName());
//        try {
//            componentScan(mainApplicationClass, beanFactory);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            throw new RuntimeException(e);
//        }
//        //这一步是在配置路由表
//        Map<String, String> urlMap = new ConcurrentHashMap<>();
//        Map<String, Object> iocContainer = beanFactory.getIocContainer();
//        //有些类在aop后置处理器会被替换,所以要检查是不是cglib代理类,是的话要去看他的父类也就是原生类,要不然扫描不到注解,因为注解不会继承
//        for (String clazz : iocContainer.keySet()) {
//            try {
//                Class<?> temp = Class.forName(clazz);
//                if (temp.getName().contains("$$EnhancerByCGLIB")) {
//                    processClassForMapping(temp.getSuperclass(), urlMap);
//                } else {
//                    processClassForMapping(temp, urlMap);
//                }
//            } catch (Exception ignored) {
//
//            }
//
//        }
//        beanFactory.put("urlmapping", urlMap);
//        //运行时判断环境,依照自定义的条件注解选择运行容器,有我写的socketserver和tomcat
//        ServerRunner server = (ServerRunner) beanFactory.getBean(ServerRunner.class.getName());
//        server.run();
    }


//    private void componentScan(Class<?> mainClass, ApplicationContext myContext) throws Exception {
//        //xml方式加载beans,弃用
//        XMLBeansLoader xmlBeansLoader = new XMLBeansLoader();
////        SimpleMyBeanDefinitionRegistry registry = new SimpleMyBeanDefinitionRegistry();
//        List<Class<? extends Annotation>> annotations = new ArrayList<>();
//        annotations.add(MyController.class);
//        annotations.add(MyService.class);
//        annotations.add(MyComponent.class);
//        annotations.add(MyConfig.class);
//        annotations.add(MyWebSocketConfig.class);
//        annotations.add(MyAspect.class);
//        Set<Class<?>> annotatedClasses = scanner.findAnnotatedClassesList(annotations, mainClass.getPackageName());
//        //首先我们检测main方法的包下符合注解要求的类
//        List<Class<BeanFactoryPostProcessor>> beanFactoryPostProcessorsClassList = new ArrayList<>();
//        //现在开始魔法时间,引入starter,实际上注解本身没有意义,其实应该是复合注解import一个大爹来调度bean的装配
//        if (AnnotationUtils.findAnnotation(mainClass, EnableAutoConfiguration.class).isPresent()) {
//            List<String> allProcessors = new ArrayList<>(autoConfigurationMap.get("BeanDefinitionRegistryPostProcessor"));
//            allProcessors.addAll(autoConfigurationMap.get("BeanFactoryPostProcessor"));
//            // 处理所有处理器类，并且检查它们是否是 BeanFactoryPostProcessor 类型的子类
//            for (String className : allProcessors) {
//                try {
//                    Class<?> clazz = Class.forName(className);
//                    processImports(annotatedClasses, clazz, beanFactoryPostProcessorsClassList);
//                    // 如果是 BeanFactoryPostProcessor，添加到处理器列表
//                    if (BeanFactoryPostProcessor.class.isAssignableFrom(clazz)) {
//                        beanFactoryPostProcessorsClassList.add((Class<BeanFactoryPostProcessor>) clazz);
//                    }
//                } catch (ClassNotFoundException e) {
//                    log.warn(e.getMessage(), e);
//                }
//            }
//
//            //处理所有通过配置文件指定的 Bean类
//            for (String className : spiMap.get("Beans")) {
//                try {
//                    Class<?> clazz = Class.forName(className);
//                    annotatedClasses.add(clazz);
//                    processImports(annotatedClasses, clazz, beanFactoryPostProcessorsClassList);
//                } catch (ClassNotFoundException e) {
//                    System.err.println("未找到类: " + className);
//                }
//            }
//        }
//
//        //递归处理所有通过@Import注解引入的类
//        for (Class<?> clazz : annotatedClasses.toArray(new Class[0])) {
//            processImports(annotatedClasses, clazz, beanFactoryPostProcessorsClassList);
//        }
//
//

    /// /        List<Class<BeanFactoryPostProcessor>> starterRegisterer = xmlBeansLoader.loadStarterClasses("plugins");
//        long startTime = System.currentTimeMillis();
//        for (Class<?> clazz : annotatedClasses) {
//            Optional<MyConfig> configOptional = AnnotationUtils.findAnnotation(clazz, MyConfig.class);
//            if (configOptional.isPresent() || FactoryBean.class.isAssignableFrom(clazz)) {
//                if (clazz.isAnnotation()) {
//                    continue;
//                }
//                MyBeanDefinition myConfigBeanDefinition = new MyBeanDefinition(clazz.getName(), clazz);
//                myContext.registerBeanDefinition(myConfigBeanDefinition.getName(), myConfigBeanDefinition);
//                Method[] methods = clazz.getDeclaredMethods();
//                for (Method method : methods) {
//                    if (method.getAnnotation(AutumnBean.class) != null) {
//                        //xxx: 当这个方法有bean注解则创建一个MyBeanDefinition
//                        MyBeanDefinition myBeanDefinition = getMyBeanDefinition(clazz, method);
//                        myContext.registerBeanDefinition(myBeanDefinition.getName(), myBeanDefinition);
//                    }
//
//                }
//                //实际上这是一个完全错误的设计,但确实可以起到作用
//                if (FactoryBean.class.isAssignableFrom(clazz)) {
//                    try {
//                        Method getObjectMethod = clazz.getMethod("getObject");
//                        MyBeanDefinition myBeanDefinition = getMyBeanDefinition(clazz, getObjectMethod);
//                        myContext.registerBeanDefinition(myBeanDefinition.getName(), myBeanDefinition);
//                    } catch (NoSuchMethodException e) {
//                        System.err.println("FactoryBean实现中没有找到getObject方法: " + clazz.getName());
//                    }
//                }
//            } else {
//                if (clazz.isAnnotation()) {
//                    continue;
//                }
//                MyBeanDefinition myBeanDefinition = new MyBeanDefinition(clazz);
//                myContext.registerBeanDefinition(myBeanDefinition.getName(), myBeanDefinition);
//            }
//        }
//
//        //xxx:  区分处理BeanDefinitionRegistryPostProcessor和BeanFactoryPostProcessor
//        List<BeanDefinitionRegistryPostProcessor> registryPostProcessors = new ArrayList<>();
//        List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
//
//        for (Class<BeanFactoryPostProcessor> postProcessorClass : beanFactoryPostProcessorsClassList) {
//            //xxx:  构造器创建一个实例
//            BeanFactoryPostProcessor postProcessor = postProcessorClass.getDeclaredConstructor().newInstance();
//            if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
//                //xxx:  是BeanDefinitionRegistryPostProcessor接口的,放一边
//                registryPostProcessors.add((BeanDefinitionRegistryPostProcessor) postProcessor);
//            } else {
//                //xxx:  是BeanFactoryPostProcessor接口的,放另一边
//                regularPostProcessors.add(postProcessor);
//            }
//        }
//
//        //xxx:  处理BeanDefinitionRegistryPostProcessor
//        invokePostProcessors(registryPostProcessors, myContext);
//
//        //xxx:  处理BeanFactoryPostProcessor
//        invokePostProcessors(regularPostProcessors, myContext);
//        for (MyBeanDefinition mb : myContext.getBeanDefinitionMap().values()) {
//
//            if (mb.getBeanClass().isAssignableFrom(BeanPostProcessor.class)) {
//
//            }
//        }
//        MyContext myContext1 = (MyContext) myContext;
//        myContext1.initIocCache(environment);
//        long endTime = System.currentTimeMillis();
//        log.info("容器花费了：{} 毫秒实例化", endTime - startTime);
//        Map<String, Object> iocContainer = myContext1.getIocContainer();
//        EventMulticaster eventMulticaster = (EventMulticaster) iocContainer.get(EventMulticaster.class.getName());
//        for (Map.Entry<String, Object> entry : iocContainer.entrySet()) {
//            Object bean = entry.getValue();
//            if (bean instanceof EventListener && !bean.getClass().isInterface()) {
//                eventMulticaster.addEventListener((EventListener<?>) bean);
//                log.warn("已注册监听器: {}", bean.getClass().getName());
//            }
//        }
//        IocInitEvent event = new IocInitEvent("容器初始化结束", endTime - startTime);
//        eventMulticaster.publishEvent(event);
//    }
//
//    private void processImports(Set<Class<?>> annotatedClasses, Class<?> clazz, List<Class<BeanFactoryPostProcessor>> beanFactoryPostProcessorsClassList) {
//        Optional<Import> importAnnotations = AnnotationUtils.findAnnotation(clazz, Import.class);
//
//        if (importAnnotations.isPresent()) {
//            for (Class<?> importClass : importAnnotations.get().value()) {
//                    if (importClass.isInterface() || importClass.isAnnotation()) {
//                        throw new RuntimeException("Import注解不能引入接口或注解: " + importClass.getName());
//                    }
//                    log.info("通过Import机制导入了{}", importClass.getName());
//                    annotatedClasses.add(importClass);
//
//                    if (BeanFactoryPostProcessor.class.isAssignableFrom(importClass)) {
//                        beanFactoryPostProcessorsClassList.add((Class<BeanFactoryPostProcessor>) importClass);
//                    }
//
//                    processImports(annotatedClasses, importClass, beanFactoryPostProcessorsClassList);
//                }
//
//        }
//    }
//
//    private void invokePostProcessors(List<?> postProcessors, BeanDefinitionRegistry registry) throws Exception {
//        //xxx:  处理实现了PriorityOrdered接口的
//        List<Object> priorityOrderedPostProcessors = new ArrayList<>();
//        for (Object postProcessor : postProcessors) {
//            if (postProcessor instanceof PriorityOrdered) {
//                priorityOrderedPostProcessors.add(postProcessor);
//            }
//        }
//        for (Object postProcessor : priorityOrderedPostProcessors) {
//            invokePostProcessor(postProcessor, registry);
//        }
//        postProcessors.removeAll(priorityOrderedPostProcessors);
//
//        //xxx:  处理实现了Ordered接口的
//        List<Object> orderedPostProcessors = new ArrayList<>();
//        for (Object postProcessor : postProcessors) {
//            if (postProcessor instanceof Ordered) {
//                orderedPostProcessors.add(postProcessor);
//            }
//        }
//        for (Object postProcessor : orderedPostProcessors) {
//            invokePostProcessor(postProcessor, registry);
//        }
//        postProcessors.removeAll(orderedPostProcessors);
//
//        //xxx:  处理其他所有的
//        for (Object postProcessor : postProcessors) {
//            invokePostProcessor(postProcessor, registry);
//        }
//    }
//
//    private void invokePostProcessor(Object postProcessor, BeanDefinitionRegistry registry) throws Exception {
//        if (postProcessor instanceof EarlyEnvironmentAware) {
//            ((EarlyEnvironmentAware) postProcessor).setEnvironment(environment);
//        }
//        if (postProcessor instanceof EarlyBeanFactoryAware) {
//            ((EarlyBeanFactoryAware) postProcessor).setBeanFactory(beanFactory);
//        }
//        if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
//            ((BeanDefinitionRegistryPostProcessor) postProcessor).postProcessBeanDefinitionRegistry(scanner, registry);
//        } else if (postProcessor instanceof BeanFactoryPostProcessor) {
//            ((BeanFactoryPostProcessor) postProcessor).postProcessBeanFactory(scanner, registry);
//        }
//    }
//
//    private void processClassForMapping(Class<?> clazz, Map<String, String> urlMap) {
//        if (clazz.isAnnotationPresent(MyController.class)) {
//            for (Method method : clazz.getDeclaredMethods()) {
//                if (method.isAnnotationPresent(MyRequestMapping.class)) {
//                    MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
//                    String value = annotation.value();
//                    log.info("映射URL: {}", value);
//                    if (!value.isEmpty()) {
//                        String fullMethodName = clazz.getName() + "." + method.getName();
//                        urlMap.put(value, fullMethodName);
//                    }
//                }
//            }
//        }
//    }

    private void initAutumnSpi() {
        try {
            spiMap = AutumnFactoriesLoader.parseConfigurations();
            autoConfigurationMap = AutumnFactoriesLoader.parseAutoConfigurations();
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

    private Class<?> deduceMainApplicationClass() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(this::findMainClass)
                .orElse(null);
    }

    private Optional<Class<?>> findMainClass(Stream<StackWalker.StackFrame> stack) {
        return stack.filter((frame) -> Objects.equals(frame.getMethodName(), "main"))
                .findFirst()
                .map(StackWalker.StackFrame::getDeclaringClass);
    }


    private <T> List<T> getAutumnFactoriesInstances(Class<T> type) {
        try {
            List<String> initializers = new ArrayList<>(spiMap.get(type.getSimpleName()));
            List<T> result = new ArrayList<>();
            for (String className : initializers) {

                Class<?> clazz = Class.forName(className);
                if (type.isAssignableFrom(clazz)) {
                    result.add((T) clazz.getDeclaredConstructor().newInstance());
                }

            }
            return result;
        } catch (Exception e) {
            log.warn("装载失败");
            return new ArrayList<>();
        }

    }

    public Set<Object> getAllSources() {
        return new LinkedHashSet<>(this.primarySources);
    }

    protected void load(ApplicationContext context, Object[] sources) {
        BeanDefinitionLoader loader = new BeanDefinitionLoader(context, sources);
        loader.load();
    }

    private void getInitOrAfterMethod(MyBeanDefinition myBeanDefinition, Method method) {
        if (method.getAnnotation(MyPostConstruct.class) != null) {
            myBeanDefinition.getInitMethodName().add(method.getName());
            myBeanDefinition.getInitMethod().add(method);
        }
        if (method.getAnnotation(MyPreDestroy.class) != null) {
            myBeanDefinition.getAfterMethodName().add(method.getName());
            myBeanDefinition.getAfterMethod().add(method);
        }
    }


    private MyBeanDefinition getMyBeanDefinition(Class clazz, Method method) {
        Class<?> returnType = method.getReturnType();
        if (returnType.equals(void.class)) {
            throw new BeanCreationException("用AutumnBean注解标记的方法返回值不能为void");
        }
        String beanName;
        AutumnBean annotation = method.getAnnotation(AutumnBean.class);
        if (annotation == null && FactoryBean.class.isAssignableFrom(clazz)) {
            beanName = returnType.getName();

        } else {
            if (method.getAnnotation(AutumnBean.class).value().isEmpty()) {
                beanName = returnType.getName();
            } else {
                beanName = method.getAnnotation(AutumnBean.class).value();
            }
        }
        MyBeanDefinition myBeanDefinition = new MyBeanDefinition(beanName, returnType);
        myBeanDefinition.setDoMethod(method);
        myBeanDefinition.setConfigurationClass(clazz);
        return myBeanDefinition;

    }


    private void checkEnv() {
        try {
            Parameter[] parameters = AutumnApplication.class.getDeclaredMethod("test", String.class, String.class).getParameters();
            String[] expectedParamNames = {"test", "test2"};
            for (int i = 0; i < parameters.length; i++) {
                String paramName = parameters[i].getName();
                if (!paramName.equals(expectedParamNames[i])) {
                    log.error("你应该打开编译参数: -parameters,现在框架无法获取真实的方法参数名");
                }

            }
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void test(String test, String test2) {

    }






}
