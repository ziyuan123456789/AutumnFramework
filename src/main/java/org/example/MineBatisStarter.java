package org.example;


import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnMVC.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnMVC.BeanLoader.ObjectFactory;
import org.example.FrameworkUtils.AutumnMVC.Ioc.AutumnStarterRegisterer;
import org.example.FrameworkUtils.AutumnMVC.Ioc.MyContext;
import org.example.FrameworkUtils.Exception.BeanCreationException;
import org.example.FrameworkUtils.Orm.MineBatis.MapperUtils;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.04
 */
@Slf4j
public class MineBatisStarter implements AutumnStarterRegisterer {
    private MyContext myContext = MyContext.getInstance();

    @Override
    public void postProcessBeanDefinitionRegistry(AnnotationScanner scanner, Map<String, MyBeanDefinition> registry) throws Exception {
        log.info(this.getClass().getSimpleName() + "从xml中加载,现在要干预BeanDefinition的生成");
        List<Class<?>> classSet = scanner.findAnnotatedClasses(myContext.get("packageUrl").toString(), MyMapper.class);
        for (Class<?> clazz : classSet) {
            MyBeanDefinition myBeanDefinition = new MyBeanDefinition();
            myBeanDefinition.setName(clazz.getName());
            myBeanDefinition.setBeanClass(clazz);
            myBeanDefinition.setStarter(true);
            myBeanDefinition.setStarterMethod(createFactoryMethod(clazz));
            registry.put(clazz.getName(), myBeanDefinition);
        }
    }

    @Override
    public ObjectFactory<?> createFactoryMethod(Class<?> beanClass) throws Exception {
        return () -> {
            try {
                return MapperUtils.init(beanClass);
            } catch (Exception e) {
                log.error("创建MapperBean实例失败", e);
                throw new BeanCreationException("创建MapperBean实例失败", e);
            }
        };
    }
}
