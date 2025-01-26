package org.example.FrameworkUtils.AutumnCore.Ioc;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationMetadata;

import java.util.Optional;

/**
 * @author ziyuan
 * @since 2025.01
 */

//条件注解处理器,看看需不需要封装成bean定义
public class ConditionEvaluator {

    private final BeanDefinitionRegistry beanDefinitionRegistry;

    public ConditionEvaluator(BeanDefinitionRegistry registry) {
        this.beanDefinitionRegistry = registry;
    }

    public boolean shouldSkip(AnnotationMetadata metadata) {
        Optional<MyConditional> annotationOptional = metadata.isAnnotated(MyConditional.class);
        if (annotationOptional.isPresent()) {
            MyConditional myConditional = annotationOptional.get();
            for (Class<?> conditionClass : myConditional.value()) {
                try {
                    MyCondition condition = (MyCondition) conditionClass.getConstructor().newInstance();
                    return !condition.matches((ApplicationContext) beanDefinitionRegistry, metadata.getClass());
                } catch (Exception e) {
                    throw new RuntimeException("条件注解运行异常");
                }
            }
        }
        return false;
    }
}
