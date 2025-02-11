package org.example.FrameworkUtils.AutumnCore.Event;

/**
 * @author ziyuan
 * @since 2024.11
 */
public class IocInitEvent extends ApplicationEvent {
    private final Object source;
    private final Long time;

    public IocInitEvent(Object source, Long time) {
        super(source, time);
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
