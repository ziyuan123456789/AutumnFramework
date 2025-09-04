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


    //实例刚刚被创建但此时还未加载任何配置环境信息也还未准备好
    void starting(BootstrapContext bootstrapContext, Class<?> mainApplicationClass);


    //环境已经准备好
    void environmentPrepared(ConfigurableEnvironment environment);


    //容器创建好之后
    void contextPrepared(ApplicationContext context);


    //容器已经被完全加载
    void contextLoaded(ApplicationContext context);


    void finished(ApplicationContext application, Exception exception);

}

