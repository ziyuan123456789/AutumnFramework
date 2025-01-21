package org.example.FrameworkUtils.AutumnCore.Event.Listener;

import org.example.FrameworkUtils.AutumnCore.Bootstrap.BootstrapContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyContext;
import org.example.FrameworkUtils.AutumnCore.env.Environment;


/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * 如果说ApplicationListener耳朵很尖,那么AutumnApplicationRunListener只会比她更厉害
 * 据说是因为AutumnApplicationRunListener的老公在AutumnApplication上班
 */
public interface AutumnApplicationRunListener {


    void starting(BootstrapContext bootstrapContext, Class<?> mainApplicationClass);


    void environmentPrepared(Environment environment);


    void contextPrepared(MyContext context);


    void contextLoaded(MyContext context);


    void finished(MyContext application, Exception exception);

}

