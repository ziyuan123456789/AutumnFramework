package org.example.FrameworkUtils.AutumnCore.Ioc;

/**
 * @author ziyuan
 * @since 2024.05
 */

/**
 * 接口代表一种约定俗成的规定,那么注解就是走后门
 * 所以你声明了Ordered/PriorityOrdered 也比不上人家@Order强
 */
public interface Ordered {
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;
    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    int getOrder();
}
