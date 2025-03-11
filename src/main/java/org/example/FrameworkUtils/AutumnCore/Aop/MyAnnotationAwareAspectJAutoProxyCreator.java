package org.example.FrameworkUtils.AutumnCore.Aop;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyOrder;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.EnvironmentAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.SmartInstantiationAwareBeanPostProcessor;
import org.example.FrameworkUtils.AutumnCore.env.Environment;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ziyuan
 * @since 2024.05
 */

/**
 * AOP的实现
 * 代理类本身不会被依赖注入,而是包裹一个成熟的bean,来进行代替
 */
@Slf4j
@MyComponent
@MyOrder(1)
public class MyAnnotationAwareAspectJAutoProxyCreator implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware, EnvironmentAware {

    private ApplicationContext beanFactory;

    /**
     * 在新版的容器中,AnnotationAwareAspectJAutoProxyCreator不能被自动依赖注入,因为他必须排在第一顺位来决定是否替换Bean实现类,AutowiredAnnotationBeanPostProcessor无法访问到他
     * 所以我们需要实现EnvironmentAware/BeanFactoryAware接口来手动设置依赖
     */
    private boolean cglibClassOutPut;

    private Set<String> factoriesName = new HashSet<>();

    private List<AutumnAopFactory> factories = new ArrayList<>();

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
        boolean shouldProxy = factories.stream()
                .anyMatch(factory -> factory.shouldNeedAop(beanClass, beanFactory));
        if (!shouldProxy) {
            return (T) currentResult;
        }

        enhancer.setCallback(new MethodInterceptor() {
            private final Object target = currentResult;

            @Override
            public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                Object result = null;
                boolean methodInvoked = false;

                for (AutumnAopFactory factory : factories) {
                    if (method.getDeclaringClass() != Object.class && factory.shouldIntercept(method, beanClass, beanFactory)) {
                        try {
                            factory.doBefore(target, method, args);
                        } catch (Exception e) {
                            factory.doThrowing(target, method, args, e);
                            return null;
                        }
                    }
                }

                for (AutumnAopFactory factory : factories) {
                    if (method.getDeclaringClass() != Object.class && factory.shouldIntercept(method, beanClass, beanFactory)) {
                        try {
                            result = factory.intercept(target, method, args, methodProxy);
                            if (result != null) {
                                methodInvoked = true;
                                break;
                            }
                        } catch (Exception e) {
                            factory.doThrowing(target, method, args, e);
                        }
                    }
                }

                if (!methodInvoked) {
                    try {
                        result = method.invoke(target, args);
                    } catch (Exception e) {
                        Exception exception = null;
                        for (AutumnAopFactory factory : factories) {
                            if (method.getDeclaringClass() != Object.class && factory.shouldIntercept(method, beanClass, beanFactory)) {
                                factory.doThrowing(target, method, args, e);
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
                            factory.doAfter(target, method, args);
                        } catch (Exception e) {
                            factory.doThrowing(target, method, args, e);
                        }
                    }
                }

                return result;
            }
        });

        if (currentResult != null) {
            enhancer.setClassLoader(currentResult.getClass().getClassLoader());
        }

        return (T) enhancer.create();
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

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) {
        for (MyBeanDefinition mb : beanFactory.getBeanDefinitionMap().values()) {
            if (factoriesName.contains(mb.getName())) {
                continue;
            }
            factoriesName.add(mb.getName());
            if (AutumnAopFactory.class.isAssignableFrom(mb.getBeanClass())) {
                factories.add((AutumnAopFactory) beanFactory.getBean(mb.getName()));
            }
        }
        List<AutumnAopFactory> neededFactories = shouldCreateProxy(factories, bean.getClass());
        if (!neededFactories.isEmpty()) {
            bean = create(neededFactories, bean.getClass(), bean);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return getEarlyBeanReference(bean, beanName);
    }

}
