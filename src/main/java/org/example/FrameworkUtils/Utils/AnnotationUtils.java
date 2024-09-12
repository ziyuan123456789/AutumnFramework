package org.example.FrameworkUtils.Utils;

import java.lang.annotation.Annotation;
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
}