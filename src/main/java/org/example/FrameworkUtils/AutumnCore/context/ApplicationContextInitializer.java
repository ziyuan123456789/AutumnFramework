package org.example.FrameworkUtils.AutumnCore.context;

import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;

/**
 * @author ziyuan
 * @since 2025.01
 */
@FunctionalInterface
public interface ApplicationContextInitializer{
    void initialize(ApplicationContext context);
}
