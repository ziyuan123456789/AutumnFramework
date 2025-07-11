package org.example.FrameworkUtils.AutumnCore.Event;

/**
 * @author ziyuan
 * @since 2024.11
 */
public class ContextRefreshedEvent extends ApplicationEvent {

    private final Object source;

    public ContextRefreshedEvent(Object source) {
        super(source);
        this.source = source;
    }

    @Override
    public Object getSource() {
        return source;
    }

}
