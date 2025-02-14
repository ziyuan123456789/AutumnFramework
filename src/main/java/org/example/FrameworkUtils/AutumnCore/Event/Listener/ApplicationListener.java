package org.example.FrameworkUtils.AutumnCore.Event.Listener;

import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;

import java.util.EventListener;

/*
  @author ziyuan
 * @since 2025.01
 */

/**
 * 这是一个耳朵很尖的接口,在他面前你最好谨言慎行
 * 泛型仅在编译期间生效,在运行时存在泛型擦除,因此不得不拓展一个support接口来询问实现类需不需要这个事件
 * spring的面对这件事在AbstractApplicationEventMulticaster中采用了ResolvableType来解析泛型,真是高科技!
 */

public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
    void onApplicationEvent(E event);

    boolean supportsEvent(ApplicationEvent event);
}
