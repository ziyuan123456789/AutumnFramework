package org.example.FrameworkUtils.AutumnCore.Event.Publisher;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Event.Event;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.EventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ziyuan
 * @since 2024.11
 */

/**
 * 什么事都要他发布
 * 这岂不是很有权力
 * 当然了,它也可以审查一切广播
 */
@MyComponent
public class EventMulticaster implements EventPublisher {
    private final List<EventListener> listeners = new ArrayList<>();

    public void addEventListener(EventListener listener) {
        listeners.add(listener);
    }


    public void removeEventListener(EventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void publishEvent(Event event) {
//        我们可以在这里做一些审查,看看有没有什么不符合核心价值观的事件
//        StackWalker stackWalker=StackWalker.getInstance();
        for (EventListener listener : listeners) {
            if (listener.supportsEvent(event)) {
                listener.onEvent(event);
            }
        }
    }
}
