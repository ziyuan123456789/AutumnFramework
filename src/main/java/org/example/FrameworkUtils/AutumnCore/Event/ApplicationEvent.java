package org.example.FrameworkUtils.AutumnCore.Event;

/**
 * @author ziyuan
 * @since 2025.01
 */


public abstract class ApplicationEvent implements Event {
    private final Object source;
    private final Long time;

    public ApplicationEvent(Object source, Long time) {
        this.source = source;
        this.time = time;
    }

    @Override
    public Object getSource() {
        return source;
    }

    public Long getTime() {
        return time;
    }
}
