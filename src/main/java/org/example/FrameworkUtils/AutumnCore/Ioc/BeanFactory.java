package org.example.FrameworkUtils.AutumnCore.Ioc;

/**
 * @author ziyuan
 * @since 2025.01
 */
public interface BeanFactory {

    //getBean就像老板,实际上他什么事情都不干,都是委托给员工去做了
    Object getBean(String name);

    <T> T getBean(Class<T> requiredType);
}
