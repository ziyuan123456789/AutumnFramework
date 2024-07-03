package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.EnableAutoSpiConfiguration;
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
    AutumnBeanFactory beanFactory ;
    AnnotationScanner scanner = new AnnotationScanner();

    //xxx:模板方法,以后会拓展
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

        //xxx:保存主类的包名,也就是默认扫描的包
        beanFactory.put("packageUrl", mainClass.getPackageName());
        try {
            componentScan(mainClass, beanFactory);
        } catch (Exception e) {
            log.error(String.valueOf(e));
            throw new RuntimeException(e);
        }
        Map<String, String> urlMap = new ConcurrentHashMap<>();
        Map<String, Object> iocContainer = beanFactory.getIocContainer();
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
        ServerRunner server = (ServerRunner) beanFactory.getBean(ServerRunner.class.getName());
        server.run();
    }

    private void componentScan(Class<?> mainClass, AutumnBeanFactory myContext) throws Exception {
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
        if (mainClass.getAnnotation(EnableAutoSpiConfiguration.class) != null) {
            //xxx:启动自动装配
            AutumnFactoriesLoader.loadFactories();
        }
        List<Class<BeanFactoryPostProcessor>> starterRegisterer = xmlBeansLoader.loadStarterClasses("plugins");
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

        for (Class<BeanFactoryPostProcessor> postProcessorClass : starterRegisterer) {
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
