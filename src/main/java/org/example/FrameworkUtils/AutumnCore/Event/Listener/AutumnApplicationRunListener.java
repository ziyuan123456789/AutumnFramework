package org.example.FrameworkUtils.AutumnCore.Event.Listener;

import org.example.FrameworkUtils.AutumnCore.Bootstrap.BootstrapContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyContext;


/**
 * @author ziyuan
 * @since 2025.01
 */
public interface AutumnApplicationRunListener {


    void starting(BootstrapContext bootstrapContext, Class<?> mainApplicationClass);


//    void environmentPrepared(Environment environment);


    void contextPrepared(MyContext context);


    void contextLoaded(MyContext context);


    void finished(MyContext application, Exception exception);

}

