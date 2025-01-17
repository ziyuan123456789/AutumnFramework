package org.example.FrameworkUtils.AutumnCore.Bootstrap;

/**
 * @author ziyuan
 * @since 2025.01
 */

public interface BootstrapRegistry {
    <T> void register(Class<T> type, InstanceSupplier<T> instanceSupplier);

    <T> boolean isRegistered(Class<T> type);

    <T> InstanceSupplier<T> getRegisteredInstanceSupplier(Class<T> type);
}
