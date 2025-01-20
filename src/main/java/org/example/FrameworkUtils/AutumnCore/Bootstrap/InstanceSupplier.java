package org.example.FrameworkUtils.AutumnCore.Bootstrap;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * 看看,写代码都不用那么着急,真正调用的时候再操心吧,相信后人的智慧
 */
@FunctionalInterface
public interface InstanceSupplier<T> {
    static <T> InstanceSupplier<T> of(T instance) {
        return (registry) -> instance;
    }

    T get(ConfigurableBootstrapContext context);

}
