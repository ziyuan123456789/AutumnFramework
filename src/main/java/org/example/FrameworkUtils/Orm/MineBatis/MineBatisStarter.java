package org.example.FrameworkUtils.Orm.MineBatis;


import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnMVC.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnMVC.BeanLoader.ObjectFactory;
import org.example.FrameworkUtils.AutumnMVC.Ioc.AutumnStarterRegisterer;
import org.example.FrameworkUtils.AutumnMVC.Ioc.MyContext;
import org.example.FrameworkUtils.Exception.BeanCreationException;
import org.example.FrameworkUtils.Orm.MineBatis.Io.Resources;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSession;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactory;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author ziyuan
 * @since 2024.04
 */
@Slf4j
public class MineBatisStarter implements AutumnStarterRegisterer {
    static {
        Properties p = new Properties(System.getProperties());
        p.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
        p.put("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "OFF");
        System.setProperties(p);
    }

    private final MyContext myContext = MyContext.getInstance();

    @Override
    public void postProcessBeanDefinitionRegistry(AnnotationScanner scanner, Map<String, MyBeanDefinition> registry) throws Exception {
        log.info("{}从xml中加载,现在要干预BeanDefinition的生成", this.getClass().getSimpleName());
        String minebatisXml = myContext.getProperties().getProperty("MineBatis-configXML");
        InputStream inputStream;
        if (minebatisXml == null || minebatisXml.isEmpty()) {
            inputStream = Resources.getResourceAsSteam("minebatis-config.xml");
        } else {
            inputStream = Resources.getResourceAsSteam(minebatisXml);
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        //xxx:确定工厂后生产session
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Set<Class<?>> classSet = sqlSessionFactory.getConfiguration().getMapperLocations();
        for (Class<?> clazz : classSet) {
            MyBeanDefinition myBeanDefinition = new MyBeanDefinition();
            myBeanDefinition.setName(clazz.getName());
            myBeanDefinition.setBeanClass(clazz);
            myBeanDefinition.setStarter(true);
            myBeanDefinition.setStarterMethod(createFactoryMethod(clazz, sqlSession));
            registry.put(clazz.getName(), myBeanDefinition);
        }
    }


    public ObjectFactory<?> createFactoryMethod(Class<?> beanClass, SqlSession sqlSession) throws Exception {
        return () -> {
            try {
                return sqlSession.getMapper(beanClass);
            } catch (Exception e) {
                log.error("创建MapperBean实例失败", e);
                throw new BeanCreationException("创建MapperBean实例失败", e);
            }
        };
    }
}
