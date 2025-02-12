package org.example.FrameworkUtils.AutumnCore.Event;

import lombok.EqualsAndHashCode;

/**
 * @author ziyuan
 * @since 2025.01
 */

@EqualsAndHashCode(callSuper = false)
public abstract class ApplicationEvent extends EventObject {

    private final long timestamp;

    public ApplicationEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public Object getSource() {
        return source;
    }

    public final long getTimestamp() {
        return this.timestamp;
    }


}
