package org.example.FrameworkUtils.AutumnCore.Event;

import org.example.FrameworkUtils.AutumnCore.Event.Listener.AbstractApplicationEventMulticaster;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactory;

/**
 * @author ziyuan
 * @since 2025.02
 */
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

    public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
        super(beanFactory);
    }
}
