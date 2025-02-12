package org.example.FrameworkUtils.AutumnCore.Event;

/**
 * @author ziyuan
 * @since 2024.07
 */

import lombok.Data;

/**
 * 以前,车马很慢,一个线程只能干一件事
 * 后来什么 io复用,协程出现了,现在又来了Event
 * 线程们想躺一会休息休息都不行,不知道从来的Event总是要唤醒他们去干活
 */
@Data
public class EventObject implements java.io.Serializable {

    @java.io.Serial
    private static final long serialVersionUID = 5516075349620653480L;

    protected transient Object source;

    public EventObject(Object source) {
        if (source == null) {
            throw new IllegalArgumentException("null source");
        }

        this.source = source;
    }
}
