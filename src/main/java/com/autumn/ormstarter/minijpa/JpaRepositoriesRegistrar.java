package com.autumn.ormstarter.minijpa;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.ObjectFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanDefinitionRegistry;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanDefinitionRegistryPostProcessor;
import org.example.FrameworkUtils.Exception.BeanCreationException;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSession;

import java.lang.reflect.Method;

/**
 * @author ziyuan
 * @since 2024.09
 */
@Slf4j
public class JpaRepositoriesRegistrar implements BeanDefinitionRegistryPostProcessor {

    private ApplicationContext beanFactory;

    public ObjectFactory<?> createFactoryMethod(Class<?> beanClass) {
        return () -> {
            try {

                SqlSession sqlSession= (SqlSession) beanFactory.getBean(SqlSession.class.getName());
                return sqlSession.getMapper(beanClass);
            } catch (Exception e) {
                log.error("创建MapperBean实例失败", e);
                throw new BeanCreationException("创建MapperBean实例失败", e);
            }
        };
    }

    @Override
    public void postProcessBeanDefinitionRegistry(AnnotationScanner scanner, BeanDefinitionRegistry registry) throws Exception {
        log.info("{}从配置文件或自动装配机制加载,提前干预BeanDefinition的生成,实现了BeanDefinitionRegistryPostProcessor接口", this.getClass().getSimpleName());
        try {
            Class<?> clazz = Class.forName("org.example.FrameworkUtils.AutumnCore.Ioc.MyContext");
            Method getInstanceMethod = clazz.getDeclaredMethod("getInstance");
            getInstanceMethod.setAccessible(true);
            beanFactory = (ApplicationContext) getInstanceMethod.invoke(null);
        } catch (Exception e) {
            log.error(String.valueOf(e));
            throw new RuntimeException(e);
        }


        AnnotationScanner.findImplByInterface((String) beanFactory.get("packageUrl"), JpaRepository.class).forEach(jpaRepository -> {
            MyBeanDefinition myBeanDefinition = new MyBeanDefinition();
            log.info("注册JpaRepository:{}", jpaRepository.getName());
            myBeanDefinition.setName(jpaRepository.getName());
            myBeanDefinition.setBeanClass(jpaRepository);
            myBeanDefinition.setStarter(true);
            myBeanDefinition.setStarterMethod(createFactoryMethod(jpaRepository));
            registry.registerBeanDefinition(jpaRepository.getName(), myBeanDefinition);
        });
    }


    @Override
    public void postProcessBeanFactory(AnnotationScanner scanner, BeanDefinitionRegistry registry) throws Exception {

    }
}

