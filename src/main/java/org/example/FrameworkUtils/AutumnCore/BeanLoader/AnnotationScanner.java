package org.example.FrameworkUtils.AutumnCore.BeanLoader;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAspect;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyService;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.WebSocket.MyWebSocketConfig;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wsh
 */

/**
 * Scanner最近很苦恼,他觉得自己做了那么多事,你们都是我拉进来的,Scanner如是说
 * 于是他理所当然的成为了一个Bean,继续被注入到其他人体内继续发光发热
 */
@MyComponent
@Slf4j
public class AnnotationScanner implements BeanFactoryAware {
    private static ApplicationContext beanFactory;

    private final static List<Class<? extends Annotation>> defaultAnnotation = List.of(MyService.class, MyComponent.class, MyConfig.class, MyWebSocketConfig.class, MyAspect.class);


    public static List<Class> findImplByInterface(String packageName, Class interfaceClass){
        Reflections reflections = new Reflections(packageName, new SubTypesScanner());
        Set<Class<?>> subinterfaces = reflections.getSubTypesOf(interfaceClass);
        return new ArrayList<>(subinterfaces);
    }

    public static  <A extends Annotation> List<Class<?>> findAnnotatedClasses(String basePackage, Class<A> annotationClass) {
        List<Class<?>> annotatedClasses = new ArrayList<>();
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(basePackage))
                .setScanners(new SubTypesScanner(false));
        Reflections reflections = new Reflections(configurationBuilder);
        Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
        for (Class<?> clazz : allClasses) {
            if (clazz.isAnnotationPresent(annotationClass)) {
                annotatedClasses.add(clazz);
            }
        }

        return annotatedClasses;
    }

    public Set<Class<?>> findAnnotatedClassesList(List<Class<? extends Annotation>> annotationClasses, String... basePackages) {
        Set<Class<?>> annotatedClasses = new HashSet<>();

        for (String basePackage : basePackages) {
            Reflections reflections = new Reflections(basePackage);
            for (Class<? extends Annotation> annotationClass : annotationClasses) {
                Set<Class<?>> annotatedTypes = reflections.getTypesAnnotatedWith(annotationClass);
                annotatedClasses.addAll(annotatedTypes);
            }
        }
        return annotatedClasses;
    }


    public Set<Class<?>> findDefaultAnnotatedClassesList(String... basePackages) {
        Set<Class<?>> annotatedClasses = new HashSet<>();

        for (String basePackage : basePackages) {
            Reflections reflections = new Reflections(basePackage);
            for (Class<? extends Annotation> annotationClass : defaultAnnotation) {
                Set<Class<?>> annotatedTypes = reflections.getTypesAnnotatedWith(annotationClass);
                annotatedClasses.addAll(annotatedTypes);
            }
        }
        return annotatedClasses;
    }

    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        AnnotationScanner.beanFactory =beanFactory;
    }
}
