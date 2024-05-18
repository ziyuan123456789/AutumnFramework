package org.example.Aop;

import lombok.extern.slf4j.Slf4j;
import org.example.Annotations.CheckParameter;
import org.example.FrameworkUtils.AutumnCore.Annotation.EnableAop;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAspect;
import org.example.FrameworkUtils.AutumnCore.Aop.AutumnAopFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyContext;
import org.example.controller.AutumnTestController;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author wsh
 */
@Slf4j
@MyAspect
public class UserAopProxyHandler implements AutumnAopFactory {
    @Override
    public boolean shouldNeedAop(Class clazz, MyContext myContext) {
        return AutumnTestController.class.equals(clazz);
    }

    @Override
    public boolean shouldIntercept(Method method, Class clazz, MyContext myContext) {
        return method.getAnnotation(EnableAop.class) != null;
    }

    @Override
    public void doBefore(Object obj, Method method, Object[] args) {
        log.warn("用户切面方法开始预处理,切面处理器是{}处理的方法为:{}", this.getClass().getName(), method.getName());
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation.annotationType().equals(CheckParameter.class)) {
                    log.error("参数{}被拦截", args[i].getClass().getSimpleName());
                    args[i] = "AopCheck";
                }
            }
        }
//        throw new RuntimeException("AopCheck");
        return proxy.invokeSuper(obj, args);
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

