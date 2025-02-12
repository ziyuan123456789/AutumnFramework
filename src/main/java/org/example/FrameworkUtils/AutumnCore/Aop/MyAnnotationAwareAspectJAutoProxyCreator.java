package org.example.FrameworkUtils.AutumnCore.Aop;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyOrder;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.EnvironmentAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.InstantiationAwareBeanPostProcessor;
import org.example.FrameworkUtils.AutumnCore.env.Environment;
import org.example.FrameworkUtils.Exception.BeanCreationException;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ziyuan
 * @since 2024.05
 */

@Slf4j
@MyComponent
@MyOrder(1)
public class MyAnnotationAwareAspectJAutoProxyCreator implements CgLibAop, InstantiationAwareBeanPostProcessor, BeanFactoryAware, EnvironmentAware {

    private ApplicationContext beanFactory;

    /**
     * 在新版的容器中,AnnotationAwareAspectJAutoProxyCreator不能被自动依赖注入,因为他必须排在第一顺位来决定是否替换Bean实现类,AutowiredAnnotationBeanPostProcessor无法访问到他
     * 所以我们需要实现EnvironmentAware/BeanFactoryAware接口来手动设置依赖
     */
    private boolean cglibClassOutPut;

    private List<AutumnAopFactory> shouldCreateProxy(List<AutumnAopFactory> factories, Class<?> beanClass) {
        List<AutumnAopFactory> neededFactories = new ArrayList<>();
        for (AutumnAopFactory factory : factories) {
            if (factory.shouldNeedAop(beanClass, beanFactory)) {
                neededFactories.add(factory);
            }
        }
        return neededFactories;
    }


    private <T> T create(List<AutumnAopFactory> factories, Class<T> beanClass, Object currentResult) {
        saveGeneratedCGlibProxyFiles();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(currentResult != null ? currentResult.getClass() : beanClass);

        //看看检查一下是否需要代理
        boolean shouldProxy = factories.stream()
                .anyMatch(factory -> factory.shouldNeedAop(beanClass, beanFactory));

        if (!shouldProxy) {
            return (T) currentResult;
        }

        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            Object result = null;
            boolean methodInvoked = false;

            for (AutumnAopFactory factory : factories) {
                if (method.getDeclaringClass() != Object.class && factory.shouldIntercept(method, beanClass, beanFactory)) {
                    try {
                        factory.doBefore(obj, method, args);
                    } catch (Exception e) {
                        factory.doThrowing(obj, method, args, e);
                        return null;
                    }
                }
            }

            for (AutumnAopFactory factory : factories) {
                if (method.getDeclaringClass() != Object.class && factory.shouldIntercept(method, beanClass, beanFactory)) {
                    try {
                        result = factory.intercept(obj, method, args, proxy);
                        if (result != null) {
                            methodInvoked = true;
                            break;
                        }
                    } catch (Exception e) {
                        factory.doThrowing(obj, method, args, e);
                    }
                }
            }

            //如果没有任何拦截器返回非空结果那么调用实际方法
            if (!methodInvoked) {
                try {
                    result = proxy.invokeSuper(obj, args);
                } catch (Exception e) {
                    Exception exception = null;
                    for (AutumnAopFactory factory : factories) {
                        if (method.getDeclaringClass() != Object.class && factory.shouldIntercept(method, beanClass, beanFactory)) {
                            factory.doThrowing(obj, method, args, e);
                            exception = e;
                        }
                    }
                    if (exception != null) {
                        throw exception;
                    }
                }

            }

            for (AutumnAopFactory factory : factories) {
                if (method.getDeclaringClass() != Object.class && factory.shouldIntercept(method, beanClass, beanFactory)) {
                    try {
                        factory.doAfter(obj, method, args);
                    } catch (Exception e) {
                        factory.doThrowing(obj, method, args, e);
                    }
                }
            }

            return result;
        });

        if (currentResult != null) {
            enhancer.setClassLoader(currentResult.getClass().getClassLoader());
        }

        return (T) enhancer.create();

    }


    @Override
    public Object postProcessBeforeInstantiation(List<AutumnAopFactory> factories, Class<?> beanClass, String beanName, Object currentResult) {
        List<AutumnAopFactory> neededFactories = shouldCreateProxy(factories, beanClass);
        if (neededFactories.size() > 1) {
            currentResult = create(neededFactories, beanClass, currentResult);
            log.warn("成功创建{} AOP执行链,如果你没有处理好invokeSuper的条件那么很可能会出现问题", beanClass.getName());
        }
        return currentResult;
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeanCreationException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeanCreationException {
        return bean;
    }

    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void saveGeneratedCGlibProxyFiles() {
        if (!cglibClassOutPut) {
            return;
        }
        try {
            //输出生成的字节码文件
            String resourcesPath = MyAnnotationAwareAspectJAutoProxyCreator.class.getClassLoader().getResource("").getPath();
            String cglibClassesPath = resourcesPath + "cglibClasses";
            System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, cglibClassesPath);
        } catch (Exception e) {
            log.error("保存cglib代理文件失败", e);
        }

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.cglibClassOutPut = Boolean.parseBoolean(environment.getProperty("autumn.debug.cglibClassOutPut"));
    }
}
