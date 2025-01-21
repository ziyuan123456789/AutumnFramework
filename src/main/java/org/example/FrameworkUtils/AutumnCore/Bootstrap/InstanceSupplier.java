package org.example.FrameworkUtils.AutumnCore.Bootstrap;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * 相信后人的智慧,到时候会有人帮我们解决问题
 */
@FunctionalInterface
public interface InstanceSupplier<T> {
    static <T> InstanceSupplier<T> of(T instance) {
        return (registry) -> instance;
    }

    T get(ConfigurableBootstrapContext context);

}
