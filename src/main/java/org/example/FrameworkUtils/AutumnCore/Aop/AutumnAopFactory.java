package org.example.FrameworkUtils.AutumnCore.Aop;


import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author ziyuan
 * @since 2023.11
 */

/**
 * 噩梦一般的AOP执行链,鬼知道Spring那帮人到底怎么搞出来这套的
 * 我可不愿意写代码去帮用户做判断,还是你们自己来决定怎么代理,解析表达式这种事情我可干不来
 */
public interface AutumnAopFactory {

    String CGLIB_MARK = "$$EnhancerByCGLIB$$";

    boolean shouldNeedAop(Class clazz, ApplicationContext myContext);

    boolean shouldIntercept(Method method, Class clazz, ApplicationContext myContext);

    void doBefore(Object obj, Method method, Object[] args);

     default Object intercept(Object obj, Method method, Object[] args, MethodProxy pr) throws Throwable {
         return null;
     };

    void doAfter(Object obj, Method method, Object[] args);

    void doThrowing(Object obj, Method method, Object[] args,Exception e);

}
