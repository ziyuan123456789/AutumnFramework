package org.example.FrameworkUtils.AutumnCore.Event.Listener;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Event.Event;
import org.example.FrameworkUtils.AutumnCore.Event.IocInitEvent;

/**
 * @author ziyuan
 * @since 2024.11
 */

@Slf4j
@MyComponent
public class IocInitListener implements EventListener<IocInitEvent> {
    @Override
    public void onEvent(IocInitEvent event) {
        log.warn("成功接收到开机事件,开机耗费的时间为"+event.getTime());
    }

    @Override
    public boolean supportsEvent(Event event) {
        return event instanceof IocInitEvent;
    }
}
