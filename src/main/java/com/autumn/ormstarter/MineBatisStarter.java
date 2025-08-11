package com.autumn.ormstarter;


import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanDefinitionRegistry;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanDefinitionRegistryPostProcessor;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.EnvironmentAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.PriorityOrdered;
import org.example.FrameworkUtils.AutumnCore.env.Environment;
import org.example.FrameworkUtils.Orm.MineBatis.Io.Resources;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.TypeHandler;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactory;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

/**
 * @author ziyuan
 * @since 2024.04
 **/


@Slf4j
public class MineBatisStarter implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, PriorityOrdered, BeanFactoryAware {


    private ApplicationContext beanFactory;

    private Environment environment;

    static {
        Properties p = new Properties(System.getProperties());
        p.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
        p.put("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "OFF");
        System.setProperties(p);
    }

    @Override
    public void postProcessBeanFactory(AnnotationScanner scanner, BeanDefinitionRegistry registry) throws Exception {

    }


    @Override
    public void postProcessBeanDefinitionRegistry(AnnotationScanner scanner,BeanDefinitionRegistry registry) throws Exception {
        String minebatisXml = environment.getProperty("MineBatis-configXML");
        InputStream inputStream;
        if (minebatisXml == null || minebatisXml.isEmpty()) {
            inputStream = Resources.getResourceAsSteam("minebatis-config.xml");
        } else {
            inputStream = Resources.getResourceAsSteam(minebatisXml);
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        inputStream.close();
        Set<Class<?>> classSet = sqlSessionFactory.getConfiguration().getMapperLocations();
        for (Class<?> clazz : classSet) {
            MyBeanDefinition myBeanDefinition = new MyBeanDefinition();
            log.warn("{}包装Mapper:{},替换实现为{}", this.getClass().getSimpleName(), clazz.getName(), MapperFactoryBean.class);
            myBeanDefinition.setName(clazz.getName());
            myBeanDefinition.setBeanClass(MapperFactoryBean.class);
            myBeanDefinition.setConstructor(MapperFactoryBean.class.getDeclaredConstructor(Class.class));
            Object[] parameters = new Object[]{clazz};
            myBeanDefinition.setParameters(parameters);
            registry.registerBeanDefinition(clazz.getName(), myBeanDefinition);
        }

        AnnotationScanner.findAnnotatedClasses(environment.getProperty(Environment.GET_MAIN_PACKAGE), TypeHandler.class).forEach(typeHandler -> {
            MyBeanDefinition myBeanDefinition = new MyBeanDefinition();
            myBeanDefinition.setName(typeHandler.getName());
            myBeanDefinition.setBeanClass(typeHandler);
            registry.registerBeanDefinition(typeHandler.getName(), myBeanDefinition);
            log.info("{}注册TypeHandler:{}", this.getClass().getSimpleName(), typeHandler.getName());
        });
    }

    @Override
    public int getOrder() {
        return 4;
    }



    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
