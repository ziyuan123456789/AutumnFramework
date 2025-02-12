package org.example.FrameworkUtils.AutumnCore.Event.Listener;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;

/**
 * @author ziyuan
 * @since 2025.01
 */

@Slf4j
public class BaseApplicationListener implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.info("接受到容器已经刷新完成的事件{}", event.getTimestamp());
    }

    @Override
    public boolean supportsEvent(ApplicationEvent event) {
        return event instanceof ApplicationEvent;
    }
}
