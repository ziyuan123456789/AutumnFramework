package org.example.FrameworkUtils.AutumnCore.Event.Listener;

import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Event;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * BaseApplicationListener,你是真没牌面
 */
public class BaseApplicationListener implements ApplicationListener<ApplicationEvent> {



    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println();
    }
}
