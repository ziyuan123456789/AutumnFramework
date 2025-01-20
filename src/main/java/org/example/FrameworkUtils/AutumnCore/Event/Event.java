package org.example.FrameworkUtils.AutumnCore.Event;

/**
 * @author ziyuan
 * @since 2024.07
 */

/**
 * 以前,车马很慢,一个线程只能干一件事
 * 后来什么byd io复用,协程出现了,现在又来了Event
 * 线程们想躺一会休息休息都不行
 */
public interface Event {
    Object getSource();
}
