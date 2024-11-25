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
import org.example.FrameworkUtils.AutumnCore.Event.IocInitEvent;
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
import org.example.FrameworkUtils.Exception.BeanCreationException;
import org.example.FrameworkUtils.Utils.AnnotationUtils;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.WebSocket.MyWebSocketConfig;

import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ziyuan
 * @since 2023.10
 */
@Slf4j
@MyComponent
public class AutumnFrameworkRunner {
    //这个类是启动的入口,这个名字有点难听,以后会换
    AutumnBeanFactory beanFactory ;
    AnnotationScanner scanner = new AnnotationScanner();

    //xxx:模板方法,以后会拓展,会通过starter机制引入web部分来继承这个启动类,但这都是后话了,目前代码耦合的很紧很难办
    public void postProcessBeanFactory() {
    }

    public void run(Class<?> mainClass) {
        try {
            //xxx:保证容器已经存在
            Class<?> clazz = Class.forName("org.example.FrameworkUtils.AutumnCore.Ioc.MyContext");
            Method getInstanceMethod = clazz.getDeclaredMethod("getInstance");
            getInstanceMethod.setAccessible(true);
            beanFactory = (AutumnBeanFactory) getInstanceMethod.invoke(null);
        } catch(Exception e){
            log.error(String.valueOf(e));
            throw new RuntimeException(e);
        }

        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        List<String> jvmArgs = runtimeBean.getInputArguments();
        //打印一下启动的参数
        for (String arg : jvmArgs) {
            log.warn(arg);
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
        beanFactory.put("packageUrl", mainClass.getPackageName());
        try {
            componentScan(mainClass, beanFactory);
        } catch (Exception e) {
            log.error(String.valueOf(e), e);
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
        //首先我们检测mian方法的包下符合注解要求的类
        List<Class<BeanFactoryPostProcessor>> beanFactoryPostProcessorsClassList = new ArrayList<>();
        //现在开始魔法时间,引入starter,实际上注解本身没有意义,其实应该是复合注解import一个大爹来调度bean的装配
        if (mainClass.getAnnotation(EnableAutoConfiguration.class) != null) {
            // 加载自动配置类
            Map<String, List<String>> autoConfigurations = AutumnFactoriesLoader.parseConfigurations();
            List<String> allProcessors = new ArrayList<>(autoConfigurations.get("BeanDefinitionRegistryPostProcessor"));
            allProcessors.addAll(autoConfigurations.get("BeanFactoryPostProcessor"));

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
                    System.err.println("未找到类: " + className);
                }
            }

            //处理所有通过配置文件指定的 Bean类
            for (String className : autoConfigurations.get("Beans")) {
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
            MyConfig myConfig = clazz.getAnnotation(MyConfig.class);
            if (myConfig != null || FactoryBean.class.isAssignableFrom(clazz)) {
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
                    if (!value.isEmpty()) {
                        String fullMethodName = clazz.getName() + "." + method.getName();
                        urlMap.put(value, fullMethodName);
                    }
                }
            }
        }
    }

}
