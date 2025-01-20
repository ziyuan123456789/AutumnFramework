package org.example.FrameworkUtils.AutumnCore.Ioc;

import org.example.FrameworkUtils.AutumnCore.env.Environment;

/**
 * @author ziyuan
 * @since 2025.01
 */
public interface EarlyEnvironmentAware extends EnvironmentAware {
    @Override
    void setEnvironment(Environment environment);
}
