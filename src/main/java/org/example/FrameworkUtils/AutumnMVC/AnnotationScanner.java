package org.example.FrameworkUtils.AutumnMVC;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyOrder;
import org.example.FrameworkUtils.AutumnMVC.MyContext;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author wsh
 */
@MyComponent
@Slf4j
public class AnnotationScanner {
    private MyContext myContext=MyContext.getInstance();

    public <A extends Annotation> List<Class<?>> findAnnotatedClasses(String basePackage, Class<A> annotationClass) {
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

    public Set<Class<?>> findAnnotatedClassesList(String basePackage, List<Class<? extends Annotation>> annotationClasses) {
        Set<Class<?>> annotatedClasses = new HashSet<>();
        Reflections reflections = new Reflections(basePackage);
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            Set<Class<?>> annotatedTypes = reflections.getTypesAnnotatedWith(annotationClass);

            annotatedClasses.addAll(annotatedTypes);
        }
        return annotatedClasses;
    }

    public Class<?> initFilterChain() {
        Map<Class<?>, MyBeanDefinition> iocContainer = myContext.getIocContainer();
        for (Map.Entry<Class<?>, MyBeanDefinition> entry : iocContainer.entrySet()) {
            Class<?> clazz = entry.getKey();
            if (clazz.isAnnotationPresent(MyOrder.class)) {
                MyOrder myOrder = clazz.getAnnotation(MyOrder.class);
                if (myOrder.value() == 1) {
                    return clazz;
                }
            }
        }
        return null;
    }
}
