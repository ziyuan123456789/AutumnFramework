package org.example.FrameworkUtils.AutumnCore.Ioc;


import org.example.FrameworkUtils.AutumnCore.env.Environment;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * 一个比较敏感的接口,可以感知到大环境好不好
 */
public interface EnvironmentAware extends Aware {
    void setEnvironment(Environment environment);
}

