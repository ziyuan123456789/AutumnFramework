package com.autumn.AutoConfiguration;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.AutoConfiguration.ImportSelector;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationMetadata;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AutumnFactoriesLoader;
import org.example.FrameworkUtils.Utils.AnnotationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author ziyuan
 * @since 2025.08
 */
@Slf4j
public class AutoConfigurationImportSelector implements ImportSelector {

    private final List<String> autoConfigurationList = AutumnFactoriesLoader.getAutoConfigList();


    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {

        if (autoConfigurationList.isEmpty()) {
            return new String[0];
        }
        log.info("发现 {} 个自动配置候选类: {}", autoConfigurationList.size(), autoConfigurationList);

        List<String> res = new ArrayList<>();
        for (String candidateClassName : autoConfigurationList) {
            try {
                Class<?> candidateClass = Class.forName(candidateClassName);
                if (shouldBeLoaded(candidateClass)) {
                    res.add(candidateClassName);
                }

            } catch (Exception e) {
                log.warn("配置自动配置异常: {}", candidateClassName);
            }
        }

        log.info("最终生效的自动配置类: {}", res);
        return res.toArray(new String[0]);
    }

    private boolean shouldBeLoaded(Class<?> clazz) {
        Optional<ConditionalOnClass> conditional = AnnotationUtils.findAnnotation(clazz, ConditionalOnClass.class);
        if (conditional.isEmpty()) {
            log.debug("配置类 [{}] 没有 @ConditionalOnClass 注解 默认加载", clazz.getSimpleName());
            return true;
        }

        String[] requiredClassNames = conditional.get().value();
        if (requiredClassNames.length == 0) {
            return true;
        }
        for (String requiredClassName : requiredClassNames) {
            if (!existClass(requiredClassName)) {
                log.info("配置类 [{}] 因为缺少类 [{}] 而被跳过", clazz.getSimpleName(), requiredClassName);
                return false;
            }
        }
        log.info("配置类 [{}] 的所有 @ConditionalOnClass 条件都已满足 允许加载", clazz.getSimpleName());
        return true;
    }

    private boolean existClass(String className) {
        try {
            Class.forName(className, false, Thread.currentThread().getContextClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
