package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnMVC.Annotation.EnableAop;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyController;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyPostConstruct;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyPreDestroy;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyService;
import org.example.FrameworkUtils.AutumnMVC.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnMVC.Ioc.AutumnStarterRegisterer;
import org.example.FrameworkUtils.AutumnMVC.BeanLoader.XMLBeansLoader;
import org.example.FrameworkUtils.AutumnMVC.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnMVC.Ioc.MyContext;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.WebSocket.MyWebSocketConfig;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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
    MyContext myContext = MyContext.getInstance();
    public void run(Class<?> mainClass) throws ClassNotFoundException {
        myContext.put("packageUrl", mainClass.getPackageName());
        try {
            componentScan(mainClass, myContext);
        } catch (Exception e) {
            log.error(String.valueOf(e));
            throw new RuntimeException(e);
        }
        ConcurrentHashMap<String, String> urlMap = new ConcurrentHashMap<>();
        Map<String, Object> iocContainer = myContext.getIocContainer();
        for (String clazz : iocContainer.keySet()) {
            try{
                Class temp=Class.forName(clazz);
                if (temp.getName().contains("$$EnhancerByCGLIB")) {
                    processClassForMapping(temp.getSuperclass(),urlMap);
                } else {
                    processClassForMapping(temp,urlMap);
                }
            }catch (Exception e){

            }

        }
        myContext.put("urlmapping", urlMap);
        ServerRunner server = (ServerRunner) myContext.getBean(ServerRunner.class.getName());
        server.run();
    }

    private void componentScan(Class<?> mainClass, MyContext myContext) throws Exception {
        XMLBeansLoader xmlBeansLoader = new XMLBeansLoader();
        Map<String, MyBeanDefinition> beanDefinitionMap = new HashMap<>();
        AnnotationScanner scanner = new AnnotationScanner();
        List<Class<? extends Annotation>> annotations = new ArrayList<>();
        annotations.add(MyController.class);
        annotations.add(MyService.class);
        annotations.add(MyComponent.class);
//        annotations.add(MyMapper.class);
        annotations.add(MyConfig.class);
        annotations.add(MyWebSocketConfig.class);
        Set<Class<?>> annotatedClasses = scanner.findAnnotatedClassesList(mainClass.getPackageName(), annotations);
        List<Class<AutumnStarterRegisterer>> starterRegisterer = xmlBeansLoader.loadStarterClasses("plugins");
        long startTime = System.currentTimeMillis();
        log.info("ioc容器开始初始化");
        for (Class<AutumnStarterRegisterer> registerer : starterRegisterer) {
            AutumnStarterRegisterer autumnStarterRegisterer = registerer.newInstance();
            autumnStarterRegisterer.postProcessBeanDefinitionRegistry(scanner,beanDefinitionMap);
        }
        for (Class clazz : annotatedClasses) {
            MyConfig myConfig = (MyConfig) clazz.getAnnotation(MyConfig.class);
            if (myConfig != null) {
                MyBeanDefinition myConfigBeanDefinition = new MyBeanDefinition();
                myConfigBeanDefinition.setName(clazz.getName());
                if (clazz.getAnnotation(EnableAop.class) != null) {
                    myConfigBeanDefinition.setCglib(true);
                } else {
                    myConfigBeanDefinition.setCglib(false);
                }
                beanDefinitionMap.put(myConfigBeanDefinition.getName(), myConfigBeanDefinition);
                myConfigBeanDefinition.setBeanClass(clazz);
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getAnnotation(AutumnBean.class) != null) {
                        //xxx:当这个方法有bean注解则创建一个MyBeanDefinition
                        Class<?> returnType = method.getReturnType();
                        if (returnType.equals(void.class)) {
                            throw new RuntimeException("用AutumnBean注解标记的方法返回值不能为void");
                        }
                        if (returnType.isInterface()) {
                            throw new RuntimeException("用AutumnBean注解标记的方法返回值不能为接口");
                        }
                        MyBeanDefinition myBeanDefinition = new MyBeanDefinition();
                        myBeanDefinition.setDoMethod(method);
                        myBeanDefinition.setConfigurationClass(clazz);
                        for (Method returnTypeMethod : returnType.getDeclaredMethods()) {
                            if (returnTypeMethod.isAnnotationPresent(MyPostConstruct.class)) {
                                myBeanDefinition.setInitMethodName(returnTypeMethod.getName());
                                myBeanDefinition.setInitMethod(returnTypeMethod);
                            }
                            if (method.isAnnotationPresent(MyPreDestroy.class)) {
                                myBeanDefinition.setAfterMethodName(returnTypeMethod.getName());
                                myBeanDefinition.setAfterMethod(returnTypeMethod);
                            }
                        }
                        if (returnType.getAnnotation(EnableAop.class) != null) {
                            myBeanDefinition.setCglib(true);
                        } else {
                            myBeanDefinition.setCglib(false);
                        }
                        if (method.getAnnotation(AutumnBean.class).value().isEmpty()) {
                            myBeanDefinition.setName(returnType.getName());
                        } else {
                            myBeanDefinition.setName(method.getAnnotation(AutumnBean.class).value());
                        }
                        myBeanDefinition.setBeanClass(returnType);
                        beanDefinitionMap.put(myBeanDefinition.getName(), myBeanDefinition);
                    }

                }
            } else {
                MyBeanDefinition myBeanDefinition = new MyBeanDefinition();
                myBeanDefinition.setName(clazz.getName());
                EnableAop enableAop= (EnableAop) clazz.getAnnotation(EnableAop.class);
                if (enableAop!= null) {
                    String[] methods=enableAop.getMethod();
                    Class<?> clazzFactory= enableAop.getClassFactory();
                    if(clazzFactory==null || methods==null || methods.length==0){
                        throw new IllegalArgumentException("检查Aop注解参数是否加全了");
                    }
                    myBeanDefinition.setCglib(true);
                } else {
                    myBeanDefinition.setCglib(false);
                }

                myBeanDefinition.setBeanClass(clazz);
                myBeanDefinition.setName(clazz.getName());
                beanDefinitionMap.put(myBeanDefinition.getName(), myBeanDefinition);

            }
        }

        myContext.initIocCache(beanDefinitionMap);
        long endTime = System.currentTimeMillis();
        log.info("容器花费了：{} 毫秒实例化", endTime - startTime);
        //xxx:加载驱动
        Class.forName("org.example.MineBatisStarter");


    }
    private void processClassForMapping(Class<?> clazz,ConcurrentHashMap<String, String> urlMap) {
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
