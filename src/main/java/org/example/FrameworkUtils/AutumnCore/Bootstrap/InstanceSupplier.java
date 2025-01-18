package org.example.FrameworkUtils.AutumnCore.Bootstrap;

/**
 * @author ziyuan
 * @since 2025.01
 */
@FunctionalInterface
public interface InstanceSupplier<T> {
    static <T> InstanceSupplier<T> of(T instance) {
        return (registry) -> instance;
    }

    T get(ConfigurableBootstrapContext context);

}
