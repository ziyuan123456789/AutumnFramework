package org.example.FrameworkUtils.AutumnCore.Aop;

import java.util.List;

/**
 * @author ziyuan
 * @since 2024.05
 */

/**
 * 一个神秘的接口,他这么做一定有他的企图,应该?应该把!应该是的
 * 设计成这样是因为我没看过spring的aop是咋实现的,所以我只能自己猜了
 */
public interface CgLibAop {
    Object postProcessBeforeInstantiation(List<AutumnAopFactory> factories, Class<?> beanClass, String beanName, Object currentResult);
}
