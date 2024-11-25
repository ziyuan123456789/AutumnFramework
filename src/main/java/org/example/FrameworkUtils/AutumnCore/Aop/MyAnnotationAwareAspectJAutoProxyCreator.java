package org.example.FrameworkUtils.AutumnCore.Aop;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;
import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnBeanFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.InstantiationAwareBeanPostProcessor;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ziyuan
 * @since 2024.05
 */
@MyComponent
@Slf4j
public class MyAnnotationAwareAspectJAutoProxyCreator implements CgLibAop, InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private AutumnBeanFactory beanFactory;

    @Value("autumn.debug.cglibClassOutPut")
    boolean cglibClassOutPut;

    private List<AutumnAopFactory> shouldCreateProxy(List<AutumnAopFactory> factories, Class<?> beanClass) {
        List<AutumnAopFactory> neededFactories = new ArrayList<>();
        for (AutumnAopFactory factory : factories) {
            if (factory.shouldNeedAop(beanClass, beanFactory)) {
                neededFactories.add(factory);
            }
        }
        return neededFactories;
    }


    public <T> T create(List<AutumnAopFactory> factories, Class<T> beanClass, Object currentResult) {
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
                    Exception exception=null;
                    for (AutumnAopFactory factory : factories) {
                        if (method.getDeclaringClass() != Object.class && factory.shouldIntercept(method, beanClass, beanFactory)) {
                            factory.doThrowing(obj, method, args, e);
                            exception=e;
                        }
                    }
                    if(exception!=null){
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
        if (!neededFactories.isEmpty()) {
            log.warn("多个代理工厂,如果你没有处理好invokeSuper的条件那么很可能会出现问题");
            log.info("创建代理 {}", beanClass.getName());
            currentResult = create(neededFactories, beanClass, currentResult);
        }
        return currentResult;
    }



    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return null;
    }

    @Override
    public void setBeanFactory(AutumnBeanFactory beanFactory) {
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

}
