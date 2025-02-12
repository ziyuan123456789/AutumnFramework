package com.autumn.ormstarter;


import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.Import;
import org.example.FrameworkUtils.AutumnCore.Aop.JokePostProcessor;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.ObjectFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanDefinitionRegistry;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanDefinitionRegistryPostProcessor;
import org.example.FrameworkUtils.AutumnCore.Ioc.EarlyBeanFactoryAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.EnvironmentAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.PriorityOrdered;
import org.example.FrameworkUtils.AutumnCore.env.Environment;
import org.example.FrameworkUtils.Exception.BeanCreationException;
import org.example.FrameworkUtils.Orm.MineBatis.Io.Resources;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.TypeHandler;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSession;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactory;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

/**
 * @author ziyuan
 * @since 2024.04
<p>这是一个连接 ORM和框架的桥梁，提供无侵入的方式引入并配置 ORM 框架。</p>
<ol>
<li><b>扫描注册 Mapper</b>: 自动扫描并注册所有的 Mapper。</li>
<li><b>扫描注册 TypeHandler</b>: 自动扫描并注册自定义的 TypeHandler。</li>
<li><b>事务管理器的注册</b>: 事务管理器的注册由其他类完成。</li>
</ol>

<h3>依赖注入的局限性</h3>
<p>在 Spring Boot 中，即使实现了 <code>BeanDefinitionRegistryPostProcessor</code> 接口，该类依然可以进行有限的依赖注入。由于 <code>BeanDefinitionRegistryPostProcessor</code> 会优先于普通 Bean 被创建并执行，因此依赖注入存在以下限制：</p>
<ul>
<li>可以安全注入的 Bean：
<ul>
<li>Spring 核心组件（如 <code>Environment</code>, <code>ResourceLoader</code>, <code>ApplicationContext</code> 等），因为这些 Bean 在 Spring 容器的早期阶段就已被初始化。</li>
</ul>
</li>
<li>不适合直接注入的 Bean：
<ul>
<li>用户自定义的普通 Bean，因为这些 Bean 可能尚未完成初始化，或者依赖其他 <code>BeanDefinitionRegistryPostProcessor</code> 注册的 Bean。</li>
</ul>
</li>
</ul>

<h3>心智负担与设计建议</h3>
<p>由于 <code>BeanDefinitionRegistryPostProcessor</code> 可以对 <code>BeanDefinitionRegistry</code> 进行大量修改（如增删改 Bean 定义），并可能依赖其他尚未创建的 Bean，这种模式会带来较大的心智负担和潜在的依赖问题。</p>
<p>因此，在 <code>BeanDefinitionRegistry</code> 阶段进行依赖注入并不优雅，推荐的替代方案是使用 Spring 的 Aware 接口来获取早期依赖（如 <code>ApplicationContextAware</code> 或 <code>EnvironmentAware</code> 等）。</p>
 */
@Slf4j
@Import({SqlSessionFactoryBean.class, JokePostProcessor.class})
public class MineBatisStarter implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, PriorityOrdered, EarlyBeanFactoryAware {


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
            log.warn("{}包装Mapper:{}", this.getClass().getSimpleName(), clazz.getName());
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
