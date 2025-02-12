package org.example.FrameworkUtils.AutumnCore.Ioc;

/**
 * @author ziyuan
 * @since 2024.06
 */

/**
 * 赛博佛祖也许真的存在,只要你点燃赛博香火,闭上眼许个愿,beanFactory就会来到你的面前
 */
public interface BeanFactoryAware extends Aware {
    void setBeanFactory(ApplicationContext beanFactory);
}
