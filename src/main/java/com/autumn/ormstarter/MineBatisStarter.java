package com.autumn.ormstarter;


import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.Import;
import org.example.FrameworkUtils.AutumnCore.Aop.JokePostProcessor;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.ObjectFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnBeanFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanDefinitionRegistry;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanDefinitionRegistryPostProcessor;
import org.example.FrameworkUtils.AutumnCore.Ioc.PriorityOrdered;
import org.example.FrameworkUtils.Exception.BeanCreationException;
import org.example.FrameworkUtils.Orm.MineBatis.Io.Resources;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.TypeHandler;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSession;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactory;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Set;

/**
 * @author ziyuan
 * @since 2024.04
 */
@Slf4j
@Import({SqlSessionFactoryBean.class, JokePostProcessor.class})
public class MineBatisStarter implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {
    static {
        Properties p = new Properties(System.getProperties());
        p.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
        p.put("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "OFF");
        System.setProperties(p);
    }

    private AutumnBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(AnnotationScanner scanner, BeanDefinitionRegistry registry) throws Exception {

    }


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
    public void postProcessBeanDefinitionRegistry(AnnotationScanner scanner,BeanDefinitionRegistry registry) throws Exception {
        log.info("{}从配置文件或自动装配机制加载,提前干预BeanDefinition的生成,优先级为PriorityOrdered,实现了BeanDefinitionRegistryPostProcessor接口", this.getClass().getSimpleName());
        try {
            Class<?> clazz = Class.forName("org.example.FrameworkUtils.AutumnCore.Ioc.MyContext");
            Method getInstanceMethod = clazz.getDeclaredMethod("getInstance");
            getInstanceMethod.setAccessible(true);
            beanFactory = (AutumnBeanFactory) getInstanceMethod.invoke(null);
        } catch (Exception e) {
            log.error(String.valueOf(e));
            throw new RuntimeException(e);
        }
        String minebatisXml = beanFactory.getProperties().getProperty("MineBatis-configXML");

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
            myBeanDefinition.setName(clazz.getName());
            myBeanDefinition.setBeanClass(clazz);
            myBeanDefinition.setStarter(true);
            myBeanDefinition.setStarterMethod(createFactoryMethod(clazz));
            registry.registerBeanDefinition(clazz.getName(), myBeanDefinition);
        }
        AnnotationScanner.findAnnotatedClasses((String) beanFactory.get("packageUrl"), TypeHandler.class).forEach(typeHandler -> {
            MyBeanDefinition myBeanDefinition = new MyBeanDefinition();
            myBeanDefinition.setName(typeHandler.getName());
            myBeanDefinition.setBeanClass(typeHandler);
            registry.registerBeanDefinition(typeHandler.getName(), myBeanDefinition);
        });
    }
}
