package org.example.FrameworkUtils.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ziyuan
 * @since 2024.08
 */
public class AnnotationUtils {

    public static <A extends Annotation> List<A> findAllClassAnnotations(Class<?> clazz, Class<A> annotationClass) {
        Set<Annotation> visited = new HashSet<>();
        List<A> annotations = new ArrayList<>();
        findAllClassAnnotationsRecursive(clazz, annotationClass, visited, annotations);
        return annotations;
    }

    private static <A extends Annotation> void findAllClassAnnotationsRecursive(Class<?> clazz, Class<A> annotationClass, Set<Annotation> visited, List<A> result) {
        A annotation = clazz.getAnnotation(annotationClass);
        if (annotation != null) {
            result.add(annotation);
        }

        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation ann : annotations) {
            if (!visited.contains(ann)) {
                visited.add(ann);
                findAllClassAnnotationsRecursive(ann.annotationType(), annotationClass, visited, result);
            }
        }
    }

    /**
     * 查找目标类、方法、字段或参数上的目标注解及其复合注解
     *
     * @param element               被检查的目标（Class, Method, Field, Parameter 等）
     * @param targetAnnotationClass 要寻找的注解类
     * @return 找到的所有目标注解的实例列表
     */
    public static List<Annotation> findCompositeAnnotations(AnnotatedElement element, Class<? extends Annotation> targetAnnotationClass) {
        if (element == null || targetAnnotationClass == null) {
            return new ArrayList<>();
        }

        // 用于存储已找到的注解
        List<Annotation> foundAnnotations = new ArrayList<>();

        // 存储已访问过的注解，防止重复递归
        Set<Class<? extends Annotation>> visitedAnnotations = new HashSet<>();

        // 检查当前元素的直接注解和复合注解
        for (Annotation annotation : element.getAnnotations()) {
            findAnnotationsRecursively(annotation, targetAnnotationClass, foundAnnotations, visitedAnnotations);
        }

        return foundAnnotations;
    }

    /**
     * 递归查找注解上的注解是否包含目标注解
     *
     * @param annotation            当前检查的注解实例
     * @param targetAnnotationClass 目标注解类
     * @param foundAnnotations      存储找到的注解实例
     * @param visitedAnnotations    存储已访问过的注解，避免重复递归
     */
    private static void findAnnotationsRecursively(
            Annotation annotation,
            Class<? extends Annotation> targetAnnotationClass,
            List<Annotation> foundAnnotations,
            Set<Class<? extends Annotation>> visitedAnnotations
    ) {
        Class<? extends Annotation> annotationType = annotation.annotationType();

        // 避免重复检查
        if (!visitedAnnotations.add(annotationType)) {
            return;
        }

        // 如果当前注解是目标注解，加入结果列表
        if (annotationType.equals(targetAnnotationClass)) {
            foundAnnotations.add(annotation);
        }

        // 检查当前注解上的元注解
        for (Annotation metaAnnotation : annotationType.getAnnotations()) {
            findAnnotationsRecursively(metaAnnotation, targetAnnotationClass, foundAnnotations, visitedAnnotations);
        }
    }
}
