package org.example.FrameworkUtils.AutumnCore.Bootstrap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2025.01
 */
public class DefaultAutumnBootstrapContext implements AutumnBootstrapContext {

    private final Map<Class<?>, Object> registry = new HashMap<>();

    @Override
    public <T> T get(Class<T> type) {
        return (T) registry.get(type);
    }

    @Override
    public <T> void register(Class<T> type, T instance) {
        registry.put(type, instance);
    }
}
