package org.example.FrameworkUtils.AutumnCore.Bootstrap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2025.01
 */


public class DefaultAutumnBootstrapRegistry implements BootstrapRegistry {

    private final Map<Class<?>, InstanceSupplier<?>> registry = new HashMap<>();

    @Override
    public <T> void register(Class<T> type, InstanceSupplier<T> instanceSupplier) {
        registry.put(type, instanceSupplier);
    }

    @Override
    public <T> boolean isRegistered(Class<T> type) {
        return registry.containsKey(type);
    }

    @Override
    public <T> InstanceSupplier<T> getRegisteredInstanceSupplier(Class<T> type) {
        return (InstanceSupplier<T>) registry.get(type);
    }
}
