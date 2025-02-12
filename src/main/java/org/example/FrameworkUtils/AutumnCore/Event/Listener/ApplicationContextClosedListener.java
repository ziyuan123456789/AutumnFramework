package org.example.FrameworkUtils.AutumnCore.Event.Listener;

import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.ContextClosedEvent;

/**
 * @author ziyuan
 * @since 2025.02
 */
public class ApplicationContextClosedListener implements ApplicationListener<ContextClosedEvent> {

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {


    }

    @Override
    public boolean supportsEvent(ApplicationEvent event) {
        return event instanceof ContextClosedEvent;
    }
}
