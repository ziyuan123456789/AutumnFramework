package org.example.FrameworkUtils.AutumnCore.Bootstrap;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * 注册表,可以在bean容器没创建之前创建一些对象,最后可以共享到Ioc容器里
 * BootstrapContext中我不提供依赖注入,但是到了Ioc容器后就当作是一些普通的bean,如果不用后置处理器
 * 干预,那么依然会进行二次依赖注入
 */

public interface BootstrapRegistry {
    <T> void register(Class<T> type, InstanceSupplier<T> instanceSupplier);

    <T> boolean isRegistered(Class<T> type);

    <T> InstanceSupplier<T> getRegisteredInstanceSupplier(Class<T> type);
}
