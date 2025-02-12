package org.example.FrameworkUtils.AutumnCore.Ioc;

/**
 * @author ziyuan
 * @since 2025.02
 */
public interface ApplicationContextAware extends Aware {
    void setApplicationContext(ApplicationContext applicationContext);
}
