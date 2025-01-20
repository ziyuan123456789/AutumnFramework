package org.example.FrameworkUtils.AutumnCore.Event.Publisher;

import org.example.FrameworkUtils.AutumnCore.Event.Event;

/**
 * @author ziyuan
 * @since 2024.11
 */

/**
 * 权力,EventPublisher就是权力
 */
public interface EventPublisher {
    void publishEvent(Event event);
}
