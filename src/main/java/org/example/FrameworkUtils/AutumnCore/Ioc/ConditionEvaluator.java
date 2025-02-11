package org.example.FrameworkUtils.AutumnCore.Ioc;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationMetadata;

import java.util.Optional;

/**
 * @author ziyuan
 * @since 2025.01
 */

//看看需不需要封装成bean定义
@Slf4j
public class ConditionEvaluator {

    private final BeanDefinitionRegistry beanDefinitionRegistry;

    public ConditionEvaluator(BeanDefinitionRegistry registry) {
        this.beanDefinitionRegistry = registry;
    }

    //判断是否需要跳过包装,条件注解运行在包装Bean定义阶段,同时他的注解判断器也不会被自动加载为Bean,除非使用注解声明
    public boolean shouldSkip(AnnotationMetadata metadata) {
        Optional<MyConditional> annotationOptional = metadata.isAnnotated(MyConditional.class);
        if (annotationOptional.isPresent()) {
            for (Class<?> conditionClass : annotationOptional.get().value()) {
                try {
                    MyCondition condition = (MyCondition) conditionClass.getConstructor().newInstance();
                    return !condition.matches((ApplicationContext) beanDefinitionRegistry, metadata.getClass());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new RuntimeException("条件注解运行异常");
                }
            }
        }
        return false;
    }
}
