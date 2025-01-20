package org.example.FrameworkUtils.AutumnCore.Event;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * 攀了关系,要不然他怎么能叫ApplicationEvent
 */
public class ApplicationEvent implements Event {
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
