package org.example.FrameworkUtils.AutumnCore.Ioc;

import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationListener;

import java.io.Closeable;

/**
 * @author ziyuan
 * @since 2025.02
 */
public interface ConfigurableApplicationContext extends ApplicationContext, Closeable {

    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

    void addApplicationListener(ApplicationListener<ApplicationEvent> listener);

    void removeApplicationListener(ApplicationListener<ApplicationEvent> listener);

    void registerShutdownHook();


}
