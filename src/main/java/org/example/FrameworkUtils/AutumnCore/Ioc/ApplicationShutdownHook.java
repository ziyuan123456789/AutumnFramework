package org.example.FrameworkUtils.AutumnCore.Ioc;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationContextClosedListener;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationListener;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author ziyuan
 * @since 2025.02
 */
@Slf4j
public class ApplicationShutdownHook implements Runnable {

    private final Set<AnnotationConfigApplicationContext> contexts = new LinkedHashSet();

    private final ApplicationListener<?> listener = new ApplicationContextClosedListener();


    @Override
    public void run() {
        log.warn("准备关机了,ApplicationShutdownHook开始调用容器的Close方法");
        contexts.forEach(AnnotationConfigApplicationContext::close);
    }

    public void registerApplicationContext(AnnotationConfigApplicationContext context) {
        addRuntimeShutdownHook();
        synchronized (ApplicationShutdownHook.class) {
            context.addApplicationListener((ApplicationListener<ApplicationEvent>) this.listener);
            this.contexts.add(context);
        }
    }

    void addRuntimeShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this, "AutumnApplicationShutdownHook"));
    }


}
