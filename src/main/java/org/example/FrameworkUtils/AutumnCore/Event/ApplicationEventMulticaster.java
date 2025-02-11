package org.example.FrameworkUtils.AutumnCore.Event;

import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationListener;

/**
 * @author ziyuan
 * @since 2025.02
 */
public interface ApplicationEventMulticaster {
    void addApplicationListener(ApplicationListener<ApplicationEvent> listener);

    void addApplicationListenerBean(String listenerBeanName);

    void removeApplicationListener(ApplicationListener<ApplicationEvent> listener);

    void removeApplicationListenerBean(String listenerBeanName);

    void removeAllListeners();

    void multicastEvent(ApplicationEvent event);
}
