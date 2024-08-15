package org.example.FrameworkUtils.Utils;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ziyuan
 * @since 2024.08
 */
public class AnnotationUtils {

    public static <A extends Annotation> A findClassAnnotation(Class<?> clazz, Class<A> annotationClass) {
        Set<Annotation> visited = new HashSet<>();
        return findClassAnnotationRecursive(clazz, annotationClass, visited);
    }

    private static <A extends Annotation> A findClassAnnotationRecursive(Class<?> clazz, Class<A> annotationClass, Set<Annotation> visited) {
        A annotation = clazz.getAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }

        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation ann : annotations) {
            if (!visited.contains(ann)) {
                visited.add(ann);
                A found = findClassAnnotationRecursive(ann.annotationType(), annotationClass, visited);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }
}