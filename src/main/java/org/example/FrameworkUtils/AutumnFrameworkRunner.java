package org.example.FrameworkUtils;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Annotation.MyConfig;
import org.example.FrameworkUtils.Annotation.MyController;
import org.example.FrameworkUtils.Annotation.MyMapper;
import org.example.FrameworkUtils.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.Annotation.MyService;
import org.example.FrameworkUtils.Webutils.MyContext;
import org.example.FrameworkUtils.Webutils.SocketServer;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangzhiyi
 * @since 2023.10
 */
@Slf4j

public class AutumnFrameworkRunner {

    public void run(Class<?> mainClass, String[] args) {
        Thread t = new Thread(() -> {
            MyContext myContext = MyContext.getInstance();
            myContext.put("packageUrl", mainClass.getPackageName());
            try {
                componentScan(mainClass, myContext);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            ConcurrentHashMap<String, String> urlMap = new ConcurrentHashMap<>();
            Map<Class<?>, Object> iocContainer = myContext.getIocContainer();
            for (Class clazz : iocContainer.keySet()) {
                if (clazz.getName().contains("$$EnhancerByCGLIB")) {
                    Class clazzParent= clazz.getSuperclass();
                    if (clazzParent.getDeclaredAnnotation(MyController.class) != null) {
                        Method[] methods = clazzParent.getDeclaredMethods();
                        for (Method method : methods) {
                            if (method.isAnnotationPresent(MyRequestMapping.class)) {
                                MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
                                String value = annotation.value();
                                if (!"".equals(value)) {
                                    String fullMethodName = clazz.getName() + "." + method.getName();
                                    urlMap.put(value, fullMethodName);
                                }
                            }
                        }
                    }

                } else {
                    if (clazz.getDeclaredAnnotation(MyController.class) != null) {
                        Method[] methods = clazz.getDeclaredMethods();
                        for (Method method : methods) {
                            if (method.isAnnotationPresent(MyRequestMapping.class)) {
                                MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
                                String value = annotation.value();
                                if (!"".equals(value)) {
                                    String fullMethodName = clazz.getName() + "." + method.getName();
                                    urlMap.put(value, fullMethodName);
                                }
                            }
                        }
                    }
                }

            }
            myContext.put("urlmapping", urlMap);
            SocketServer server = (SocketServer) myContext.getBean(SocketServer.class);
            try {
                server.init();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);

            }
        });
        t.start();
    }

    private void componentScan(Class<?> mainClass, MyContext myContext) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        AnnotationScanner scanner = new AnnotationScanner();
        List<Class<? extends Annotation>> annotations = new ArrayList<>();
        annotations.add(MyController.class);
        annotations.add(MyService.class);
        annotations.add(MyComponent.class);
        annotations.add(MyMapper.class);
        annotations.add(MyConfig.class);
        Set<Class<?>> annotatedClasses = scanner.findAnnotatedClassesList(mainClass.getPackageName(), annotations);
        log.info("扫描如下注解:\t" + annotatedClasses);
        myContext.initIocCache(annotatedClasses);
    }

}
