package org.example.FrameworkUtils.AutumnCore.Event.Listener;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Event;

import java.util.function.Consumer;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * 这是一个耳朵很尖的接口,实现了她你的耳朵也会尖尖的
 * 那我问你,你是男的女的
 */
public interface ApplicationListener <E extends ApplicationEvent> {
    void onApplicationEvent(E event);
}
