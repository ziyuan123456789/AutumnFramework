package org.example.FrameworkUtils.Utils;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Aop.AutumnAopFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author ziyuan
 * @since 2024.08
 */
public class AnnotationUtils {

    // 寻找Bean的名字
    public static String findBeanName(Class<?> clazz) {
        if (clazz == null) {
            throw new RuntimeException();
        }
        if (clazz.getName().contains(AutumnAopFactory.CGLIB_MARK)) {
            clazz = clazz.getSuperclass();
        }
        MyComponent ann = clazz.getAnnotation(MyComponent.class);
        if (ann == null) {
            return clazz.getName();
        } else {
            if (ann.value() == null || ann.value().isEmpty()) {
                return clazz.getName();
            }
            return ann.value();
        }
    }

    // 寻找类上所有的注解
    public static List<Annotation> findAllClassAnnotations(Class<?> clazz) {
        if (clazz == null) {
            return new ArrayList<>();
        }
        if (clazz.getName().contains(AutumnAopFactory.CGLIB_MARK)) {
            clazz = clazz.getSuperclass();
        }

        List<Annotation> foundAnnotations = new ArrayList<>();
        Set<Class<? extends Annotation>> visitedAnnotations = new HashSet<>();

        for (Annotation annotation : clazz.getAnnotations()) {
            findAnnotationsRecursively(annotation, annotation.annotationType(), foundAnnotations, visitedAnnotations);
        }

        return foundAnnotations;
    }

    // 查找类上的指定注解
    public static <T extends Annotation> Optional<T> findAnnotation(Class<?> clazz, Class<T> annotationClass) {
        if (clazz.getName().contains(AutumnAopFactory.CGLIB_MARK)) {
            clazz = clazz.getSuperclass();
        }
        Set<Annotation> visited = new HashSet<>();
        return findFirstClassAnnotationRecursive(clazz, annotationClass, visited);
    }

    // 查找指定类的所有注解
    public static <A extends Annotation> List<A> findAnnotation(String clazzName, Class<A> annotationClass) {
        try {
            Class<?> clazz = Class.forName(clazzName);
            Set<Annotation> visited = new HashSet<>();
            List<A> annotations = new ArrayList<>();
            findAllClassAnnotationsRecursive(clazz, annotationClass, visited, annotations);
            return annotations;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // 查找指定类的注解(例如import可能有很多)
    public static <A extends Annotation> List<A> findAnnotations(Class clazz, Class<A> annotationClass) {
        Set<Annotation> visited = new HashSet<>();
        List<A> annotations = new ArrayList<>();
        findAllClassAnnotationsRecursive(clazz, annotationClass, visited, annotations);
        return annotations;
    }

    // 递归查找所有注解
    private static <A extends Annotation> void findAllClassAnnotationsRecursive(Class<?> clazz, Class<A> annotationClass, Set<Annotation> visited, List<A> result) {
        A annotation = clazz.getAnnotation(annotationClass);
        if (annotation != null) {
            result.add(annotation);
        }

        for (Annotation ann : clazz.getAnnotations()) {
            if (!visited.contains(ann)) {
                visited.add(ann);
                findAllClassAnnotationsRecursive(ann.annotationType(), annotationClass, visited, result);
            }
        }
    }

    // 递归查找第一个注解
    private static <A extends Annotation> Optional<A> findFirstClassAnnotationRecursive(
            Class<?> clazz,
            Class<A> annotationClass,
            Set<Annotation> visited) {

        A annotation = clazz.getAnnotation(annotationClass);
        if (annotation != null) {
            return Optional.of(annotation);
        }

        for (Annotation ann : clazz.getAnnotations()) {
            if (!visited.contains(ann)) {
                visited.add(ann);
                Optional<A> result = findFirstClassAnnotationRecursive(ann.annotationType(), annotationClass, visited);
                if (result.isPresent()) {
                    return result;
                }
            }
        }

        return Optional.empty();
    }

    // 递归查找注解上的注解是否包含目标注解
    private static void findAnnotationsRecursively(
            Annotation annotation,
            Class<? extends Annotation> targetAnnotationClass,
            List<Annotation> foundAnnotations,
            Set<Class<? extends Annotation>> visitedAnnotations
    ) {
        Class<? extends Annotation> annotationType = annotation.annotationType();

        if (!visitedAnnotations.add(annotationType)) {
            return;
        }

        if (annotationType.equals(targetAnnotationClass)) {
            foundAnnotations.add(annotation);
        }

        for (Annotation metaAnnotation : annotationType.getAnnotations()) {
            findAnnotationsRecursively(metaAnnotation, targetAnnotationClass, foundAnnotations, visitedAnnotations);
        }
    }
}
