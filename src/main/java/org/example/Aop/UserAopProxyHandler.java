package org.example.Aop;

import lombok.extern.slf4j.Slf4j;
import org.example.Annotations.CheckParameter;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAspect;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyService;
import org.example.FrameworkUtils.AutumnCore.Aop.AutumnAopFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnBeanFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author wsh
 */
@Slf4j
@MyAspect
public class UserAopProxyHandler implements AutumnAopFactory {
    @Override
    public boolean shouldNeedAop(Class clazz, AutumnBeanFactory myContext) {
        return clazz.getAnnotation(MyService.class) != null;

    }

    @Override
    public boolean shouldIntercept(Method method, Class clazz, AutumnBeanFactory myContext) {
        return  true;
    }

    @Override
    public void doBefore(Object obj, Method method, Object[] args) {
        log.warn("用户切面方法开始预处理,切面处理器是{}处理的方法为:{}", this.getClass().getSimpleName(), method.getName());
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation.annotationType().equals(CheckParameter.class)) {
                    log.error("参数{}被拦截", args[i].getClass().getSimpleName());
                    args[i] = "AopCheck";
                }
            }
        }
    }



    @Override
    public void doAfter(Object obj, Method method, Object[] args) {
        log.info("用户自定义逻辑执行结束");
    }

    @Override
    public void doThrowing(Object obj, Method method, Object[] args,Exception e) {
        log.error("用户切面方法抛出异常",e);
    }
}

