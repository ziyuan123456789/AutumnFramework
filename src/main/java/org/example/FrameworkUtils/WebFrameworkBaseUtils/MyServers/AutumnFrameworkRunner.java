package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.*;
import org.example.FrameworkUtils.AutumnMVC.AnnotationScanner;
import org.example.FrameworkUtils.AutumnMVC.MyBeanDefinition;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MyMapper;
import org.example.FrameworkUtils.AutumnMVC.MyContext;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.WebSocket.MyWebSocketConfig;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
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
    MyContext myContext = MyContext.getInstance();
    public void run(Class<?> mainClass) {
        myContext.put("packageUrl", mainClass.getPackageName());
        try {
            componentScan(mainClass, myContext);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException |
                 ClassNotFoundException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        ConcurrentHashMap<String, String> urlMap = new ConcurrentHashMap<>();
        Map<Class<?>, MyBeanDefinition> iocContainer = myContext.getIocContainer();
        for (Class<?> clazz : iocContainer.keySet()) {
            if (clazz.getName().contains("$$EnhancerByCGLIB")) {
                processClassForMapping(clazz.getSuperclass(),urlMap);
            } else {
                processClassForMapping(clazz,urlMap);
            }
        }
        myContext.put("urlmapping", urlMap);
        ServerRunner server = myContext.getBean(ServerRunner.class);
        server.run();
    }

    private void componentScan(Class<?> mainClass, MyContext myContext) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException, InstantiationException {
        AnnotationScanner scanner = new AnnotationScanner();
        List<Class<? extends Annotation>> annotations = new ArrayList<>();
        annotations.add(MyController.class);
        annotations.add(MyService.class);
        annotations.add(MyComponent.class);
        annotations.add(MyMapper.class);
        annotations.add(MyConfig.class);
        annotations.add(MyWebSocketConfig.class);
        Set<Class<?>> annotatedClasses = scanner.findAnnotatedClassesList(mainClass.getPackageName(), annotations);
        long startTime = System.currentTimeMillis();
        log.info("ioc容器开始初始化");
        myContext.initIocCache(annotatedClasses);
        long endTime = System.currentTimeMillis();
        log.info("容器花费了：" + (endTime - startTime) + " 毫秒实例化");

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
