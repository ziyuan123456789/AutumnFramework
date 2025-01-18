package org.example.FrameworkUtils.AutumnCore.Event.Listener;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Event;

import java.util.function.Consumer;

/**
 * @author ziyuan
 * @since 2025.01
 */
public interface ApplicationListener <E extends ApplicationEvent> {
    void onApplicationEvent(E event);

}
