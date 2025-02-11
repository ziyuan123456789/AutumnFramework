package org.example.FrameworkUtils.AutumnCore.Ioc;

import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;

/**
 * @author ziyuan
 * @since 2025.02
 */
public interface ApplicationEventPublisher {

    default void publishEvent(ApplicationEvent event) {
        this.publishEvent((Object) event);
    }

    void publishEvent(Object event);

}
