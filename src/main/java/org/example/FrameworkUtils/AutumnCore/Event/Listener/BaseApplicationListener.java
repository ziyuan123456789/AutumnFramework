package org.example.FrameworkUtils.AutumnCore.Event.Listener;

import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Event;

/**
 * @author ziyuan
 * @since 2025.01
 */
public class BaseApplicationListener implements EventListener<ApplicationEvent> {


    @Override
    public void onEvent(ApplicationEvent event) {
        System.out.println("成功接收到开机事件,开机耗费的时间为" + event.getTime());

    }

    @Override
    public boolean supportsEvent(Event event) {
        return event instanceof ApplicationEvent;
    }
}
