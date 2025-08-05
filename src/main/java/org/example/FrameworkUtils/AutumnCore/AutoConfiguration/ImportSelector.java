package org.example.FrameworkUtils.AutumnCore.AutoConfiguration;


import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationMetadata;

/**
 * @author ziyuan
 * @since 2025.08
 */
public interface ImportSelector {
    String[] selectImports(AnnotationMetadata importingClassMetadata);
}
