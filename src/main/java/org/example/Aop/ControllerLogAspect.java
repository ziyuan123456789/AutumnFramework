package org.example.Aop;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAspect;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.AutumnCore.Aop.AutumnAopFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author ziyuan
 * @since 2025.03
 */
@Slf4j
@MyAspect
public class ControllerLogAspect implements AutumnAopFactory {

    @Override
    public boolean shouldNeedAop(Class clazz, ApplicationContext myContext) {
        return clazz.getAnnotation(MyController.class) != null;
    }

    @Override
    public boolean shouldIntercept(Method method, Class clazz, ApplicationContext myContext) {
        return method.getAnnotation(MyRequestMapping.class) != null;

    }

    @Override
    public void doBefore(Object obj, Method method, Object[] args) {
        log.info(String.valueOf(method));
        log.info(Arrays.toString(args));
        log.info(String.valueOf(args.length));

    }

    @Override
    public void doAfter(Object obj, Method method, Object[] args) {
        log.info("请求结束: {}.{}", obj.getClass().getName(), method.getName());

    }

    @Override
    public void doThrowing(Object obj, Method method, Object[] args, Exception e) {
        log.error("ControllerLogAdvice: {}.{}", obj.getClass().getName(), method.getName(), e);

    }
}
