package org.example.FrameworkUtils.AutumnCore.BeanLoader;

import lombok.Getter;
import org.example.FrameworkUtils.Utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;

/**
 * @author ziyuan
 * @since 2025.01
 */
public class AnnotationMetadata {

    @Getter
    private final Class<?> clazz;

    private List<Annotation> cache;

    public AnnotationMetadata(Class<?> clazz) {
        this.clazz = clazz;
    }

    public boolean isProxyClass() {
        if (Proxy.isProxyClass(clazz)) {
            return true;
        }
        return clazz.getName().contains("$$EnhancerByCGLIB");
    }

    //复合注解也会被一起扫描
    public List<Annotation> getAllAnnotations() {
        if (cache == null) {
            cache = AnnotationUtils.findAllClassAnnotations(clazz, Annotation.class);
        }
        return cache;
    }

    public <T extends Annotation> Optional<T> isAnnotated(Class<T> annotationClass) {
        if (cache == null) {
            cache = AnnotationUtils.findAllClassAnnotations(clazz, Annotation.class);
        }

        for (Annotation ann : cache) {
            if (ann.annotationType().equals(annotationClass)) {
                return Optional.of(annotationClass.cast(ann));
            }
        }
        return Optional.empty();
    }


}
