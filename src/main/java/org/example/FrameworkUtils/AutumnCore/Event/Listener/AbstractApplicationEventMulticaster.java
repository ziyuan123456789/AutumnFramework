package org.example.FrameworkUtils.AutumnCore.Event.Listener;

import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEventMulticaster;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ziyuan
 * @since 2025.02
 */

public class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster {

    protected final List<ApplicationListener<ApplicationEvent>> listeners = new ArrayList<>();

    private final BeanFactory beanFactory;

    public AbstractApplicationEventMulticaster(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    @Override
    public void addApplicationListener(ApplicationListener<ApplicationEvent> listener) {
        listeners.add(listener);
    }

    @Override
    public void addApplicationListenerBean(String listenerBeanName) {
        ApplicationListener<ApplicationEvent> listener = (ApplicationListener<ApplicationEvent>) beanFactory.getBean(listenerBeanName);
        listeners.add(listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<ApplicationEvent> listener) {
        listeners.remove(listener);
    }

    @Override
    public void removeApplicationListenerBean(String listenerBeanName) {
        ApplicationListener<?> listener = (ApplicationListener<?>) beanFactory.getBean(listenerBeanName);
        listeners.remove(listener);
    }

    @Override
    public void removeAllListeners() {
        listeners.clear();
    }

    @Override
    public void multicastEvent(ApplicationEvent event) {
        for (ApplicationListener<ApplicationEvent> listener : listeners) {
            if (listener.supportsEvent(event)) {
                listener.onApplicationEvent(event);
            }
        }
    }
}
