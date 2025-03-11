package org.example.FrameworkUtils.AutumnCore.Ioc;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.Lazy;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyOrder;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;
import org.example.FrameworkUtils.AutumnCore.Aop.AutumnRequestProxyFactory;
import org.example.FrameworkUtils.AutumnCore.Aop.LazyBeanFactory;
import org.example.FrameworkUtils.AutumnCore.env.Environment;
import org.example.FrameworkUtils.Exception.BeanCreationException;
import org.example.FrameworkUtils.PropertiesReader.PropertiesReader;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;

import java.lang.reflect.Field;

/**
 * @author ziyuan
 * @since 2025.02
 */

@Slf4j
@MyComponent
@MyOrder(2)
public class AutowiredAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware, EnvironmentAware {

    private ApplicationContext beanFactory;

    private Environment environment;


    @Override
    public Object postProcessProperties(Object bean, String beanName) {
        Class<?> clazz = bean.getClass().getName().contains("$$")
                ? bean.getClass().getSuperclass() : bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            try {
                //标记@auto wired进行对象/接口依赖注入
                if (field.isAnnotationPresent(MyAutoWired.class)) {
                    field.setAccessible(true);
                    if (field.get(bean) != null) {
                        continue;
                    }
//                    log.info("开始依赖注入,被处理的类是{}处理的字段是{}", bean, field.getName());
                    String myAutoWired = field.getAnnotation(MyAutoWired.class).value();
                    if (field.getType().equals(AutumnRequest.class)) {
                        field.set(bean, AutumnRequestProxyFactory.createAutumnRequestProxy());
                        continue;
                    }
                    if (field.getType().equals(AutumnResponse.class)) {
                        field.set(bean, AutumnRequestProxyFactory.createAutumnResponseProxy());
                        continue;
                    }
                    if (field.isAnnotationPresent(Lazy.class)) {
                        field.set(bean, LazyBeanFactory.createLazyBeanProxy(field.getType(), () -> {
                            log.info("延迟懒加载触发,现在开始获取对象");
                            return beanFactory.getBean(field.getType().getName());
                        }));
                        continue;
                    }

                    if (myAutoWired.isEmpty()) {
                        injectDependencies(bean, field);
                    } else {
                        if (FactoryBean.class.isAssignableFrom(field.getType())) {
                            myAutoWired = "&" + myAutoWired;
                        }
                        Object dependency = beanFactory.getBean(myAutoWired);
                        if (dependency == null) {
                            log.warn("无法解析的依赖：{}", myAutoWired);
                        }
                        field.set(bean, dependency);
                    }

                }
                //xxx:进行配置文件注入
                else if (field.isAnnotationPresent(Value.class)) {
                    field.setAccessible(true);
                    injectValueAnnotation(bean, field);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new BeanCreationException();
            }

        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeanCreationException {
        return bean;
    }

    private void injectValueAnnotation(Object instance, Field field) {
        Value value = field.getAnnotation(Value.class);
        if (value == null || "".equals(value.value())) {
            log.error("没有传递内容,注入失败");
            return;
        }

        String propertyValue = environment.getProperty(value.value());
        if (propertyValue == null) {
            log.error("属性未找到,注入失败");
            return;
        }

        try {
            field.setAccessible(true);
            Object convertedValue = PropertiesReader.convertStringToType(propertyValue, field.getType());
            field.set(instance, convertedValue);
        } catch (Exception e) {
            log.error("依赖注入失败：{}", e.getMessage());
        }
    }


    private void injectDependencies(Object bean, Field field) throws IllegalAccessException {
        Class<?> fieldType = field.getType();
        if (fieldType.isInterface()) {
            Object dependency = beanFactory.getBean(fieldType);
            field.setAccessible(true);
            field.set(bean, dependency);
        } else {
            injectNormalDependency(bean, field);
        }
    }

    //xxx:注入一般Bean,依照字段查找类,从容器取出为字段赋值
    private void injectNormalDependency(Object bean, Field field) throws IllegalAccessException {
        Class<?> dependencyType = field.getType();
        String dependencyBeanName = dependencyType.getName();
        // 如果依赖类型本身是 FactoryBeanv 则希望注入 FactoryBean 本身，而非其 getObject() 的返回结果
        if (FactoryBean.class.isAssignableFrom(dependencyType)) {
            dependencyBeanName = "&" + dependencyBeanName;
        }
        Object dependency = beanFactory.getBean(dependencyBeanName);
        //xxx:去容器里看看有没有啊,如果循环依赖了?没关系,反正容器里都是单例,注入一个未成熟的bean进去也无所谓,反正内存地址都一样,结束的时候你缺少我,我缺少你,除此之外都完整,所以我们合到一起就是完整的
        if (dependency == null) {
            log.warn("无法解析的依赖：{}", dependencyType.getName());
            return;
        }
        field.setAccessible(true);
        field.set(bean, dependency);

    }


    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(org.example.FrameworkUtils.AutumnCore.env.Environment environment) {
        this.environment = environment;
    }
}
