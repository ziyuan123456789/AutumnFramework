package org.example.FrameworkUtils.AutumnCore.Bootstrap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2025.01
 */


public class DefaultBootstrapContext implements ConfigurableBootstrapContext {

    private final Map<Class<?>, Object> registry = new HashMap<>();
    private final Map<Class<?>, InstanceSupplier<?>> instanceSuppliers = new HashMap<>();

    @Override
    public <T> T get(Class<T> type) {
        T instance = (T) registry.get(type);
        if (instance != null) {
            return instance;
        }
        InstanceSupplier<?> supplier = instanceSuppliers.get(type);
        if (supplier != null) {
            instance = (T) supplier.get(this);
            registry.put(type, instance);
            return instance;
        }

        return null;
    }

    @Override
    public <T> void register(Class<T> type, T instance) {
        registry.put(type, instance);
    }

    @Override
    public <T> void register(Class<T> type, InstanceSupplier<T> instanceSupplier) {
        instanceSuppliers.put(type, instanceSupplier);
    }

    @Override
    public <T> boolean isRegistered(Class<T> type) {
        return registry.containsKey(type) || instanceSuppliers.containsKey(type);
    }

    @Override
    public <T> InstanceSupplier<T> getRegisteredInstanceSupplier(Class<T> type) {
        return (InstanceSupplier<T>) instanceSuppliers.get(type);
    }
}

