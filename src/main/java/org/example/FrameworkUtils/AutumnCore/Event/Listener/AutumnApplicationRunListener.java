package org.example.FrameworkUtils.AutumnCore.Event.Listener;

import org.example.FrameworkUtils.AutumnCore.Bootstrap.BootstrapContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.env.ConfigurableEnvironment;


/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * AutumnApplicationRunListener,监听容器的基础事件
 */
public interface AutumnApplicationRunListener {


    void starting(BootstrapContext bootstrapContext, Class<?> mainApplicationClass);


    void environmentPrepared(ConfigurableEnvironment environment);


    void contextPrepared(ApplicationContext context);


    void contextLoaded(ApplicationContext context);


    void finished(ApplicationContext application, Exception exception);

}

