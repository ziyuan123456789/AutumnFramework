package org.example.FrameworkUtils.AutumnCore.Event.Listener;

import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * 这是一个耳朵很尖的接口,实现了她你的耳朵也会尖尖的
 */
public interface ApplicationListener <E extends ApplicationEvent> {
    void onApplicationEvent(E event);
}
