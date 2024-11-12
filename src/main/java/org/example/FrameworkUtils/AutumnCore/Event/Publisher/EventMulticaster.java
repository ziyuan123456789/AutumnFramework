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
        for (EventListener listener : listeners) {
            if (listener.supportsEvent(event)) {
                listener.onEvent(event);
            }
        }
    }
}
