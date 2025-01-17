package org.example.FrameworkUtils.AutumnCore.Bootstrap;

/**
 * @author ziyuan
 * @since 2025.01
 */
public interface AutumnBootstrapContext {
    <T> T get(Class<T> type);

    <T> void register(Class<T> type, T instance);
}