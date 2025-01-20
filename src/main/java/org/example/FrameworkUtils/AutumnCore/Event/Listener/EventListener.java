package org.example.FrameworkUtils.AutumnCore.Event.Listener;

import org.example.FrameworkUtils.AutumnCore.Event.Event;

/**
 * @author ziyuan
 * @since 2024.07
 */

/**
 * 耳朵一般尖
 */
public interface EventListener<T extends Event> {
    void onEvent(T event);

    boolean supportsEvent(Event event);
}
