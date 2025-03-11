package org.example.FrameworkUtils.AutumnCore.Ioc;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPostConstruct;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPreDestroy;
import org.example.FrameworkUtils.AutumnCore.Aop.AutumnAopFactory;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;

import java.lang.reflect.Method;

/**
 * @author ziyuan
 * @since 2025.03
 */
@MyComponent
public class CommonAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private ApplicationContext beanFactory;

    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        Class<?> clazz = bean.getClass().getName().contains(AutumnAopFactory.CGLIB_MARK) ?
                bean.getClass().getSuperclass() : bean.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(MyPreDestroy.class)) {
                method.setAccessible(true);
                MyBeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                beanDefinition.getAfterMethod().add(method);
            }

            if (method.isAnnotationPresent(MyPostConstruct.class)) {
                method.setAccessible(true);
                try {
                    method.invoke(bean);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }


}
