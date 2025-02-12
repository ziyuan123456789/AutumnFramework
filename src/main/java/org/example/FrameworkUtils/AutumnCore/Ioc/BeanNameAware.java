package org.example.FrameworkUtils.AutumnCore.Ioc;

/**
 * @author ziyuan
 * @since 2024.08
 */

/**
 * 有的类比较体面,一定要知道自己的名字,所以一定要取一个优雅一点的名字
 */
public interface BeanNameAware extends Aware {
    void setBeanName(String beanName);
}
