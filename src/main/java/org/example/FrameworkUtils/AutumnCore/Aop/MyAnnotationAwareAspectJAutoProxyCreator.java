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
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            Object result = null;

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
                    } catch (Exception e) {
                        factory.doThrowing(obj, method, args, e);
                        throw e;
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

            return (T) result;
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
            log.error("创建代理 {}", beanClass.getName());
            currentResult = create(neededFactories, beanClass, currentResult);
        }
        return currentResult;
    }

//原本cglib不能二次代理,白费劲了
//    public <T> T create(AutumnAopFactory factory, Class<T> beanClass, Object currentResult) {
//        Enhancer enhancer = new Enhancer();
//        enhancer.setSuperclass(currentResult != null ? currentResult.getClass() : beanClass);
//        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
//            if (method.getDeclaringClass() != Object.class && factory.shouldIntercept(method, beanClass, beanFactory)) {
//                try {
//                    factory.doBefore(obj, method, args);
//                    Object result = factory.intercept(obj, method, args, proxy);
//                    factory.doAfter(obj, method, args);
//                    return result;
//                } catch (Exception e) {
//                    factory.doThrowing(obj, method, args, e);
//                    throw e;
//                }
//            }
//            return proxy.invokeSuper(obj, args);
//        });
//        if (currentResult != null) {
//            log.error("设置类加载器 {}", currentResult.getClass().getClassLoader());
//            enhancer.setClassLoader(currentResult.getClass().getClassLoader());
//            log.error("设置类加载器结束");
//        }
//        T t=(T) enhancer.create();
//        return t;
//    }


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
