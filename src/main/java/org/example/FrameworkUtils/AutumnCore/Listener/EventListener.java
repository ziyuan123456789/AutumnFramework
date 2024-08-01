package org.example.FrameworkUtils.AutumnCore.Listener;

/**
 * @author ziyuan
 * @since 2024.07
 */
public interface EventListener<T extends Event> {
    void onEvent(T event);
}