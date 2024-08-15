package org.example.FrameworkUtils.AutumnCore.Aop;

import java.util.List;

/**
 * @author ziyuan
 * @since 2024.05
 */
public interface CgLibAop {
    Object postProcessBeforeInstantiation(List<AutumnAopFactory> factories, Class<?> beanClass, String beanName);
}
