package org.example.FrameworkUtils.AutumnCore.Ioc;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.ComponentScan;
import org.example.FrameworkUtils.AutumnCore.Annotation.EnableAutoConfiguration;
import org.example.FrameworkUtils.AutumnCore.Annotation.Import;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAspect;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyOrder;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyService;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AutumnFactoriesLoader;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.env.Environment;
import org.example.FrameworkUtils.Exception.BeanCreationException;
import org.example.FrameworkUtils.Utils.AnnotationUtils;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.WebSocket.MyWebSocketConfig;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * 起初,BeanFactory中只有无尽的黑暗,BeanDefinitionRegistryPostProcessor听见一个声音说：你要去带领我的子民离开这地,进入应许之地
 * 于是ConfigurationClassPostProcessor被召唤,肩负起解析与包装的神圣使命
 */
@Slf4j
@MyOrder(1)
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {
    public static final String CLASS_NAME = "org.example.FrameworkUtils.AutumnCore.Ioc.ConfigurationClassPostProcessor";

    private Environment environment;

    private Map<String, List<String>> spiMap;

    private Map<String, List<String>> autoConfigurationMap;

    @Override
    public void postProcessBeanDefinitionRegistry(AnnotationScanner scanner, BeanDefinitionRegistry registry) throws Exception {
        initAutumnSpi();
        List<Class<? extends Annotation>> annotations = new ArrayList<>();
        //这些mvc的内容应该单独开个BeanDefinitionRegistryPostProcessor自行扫描,不过我希望尽早启动这个容器,所以复用老代码
        annotations.add(MyController.class);
        annotations.add(MyService.class);
        annotations.add(MyComponent.class);
        annotations.add(MyConfig.class);
        annotations.add(MyWebSocketConfig.class);
        annotations.add(MyAspect.class);
        environment.getProperty("autumn.main.sources");
        String mainPackage = environment.getProperty("autumn.main.package");
        String mainClass = environment.getProperty("autumn.main.sources");
        Set<Class<?>> annotatedClasses = scanner.findAnnotatedClassesList(annotations, mainPackage);
        for (Class<?> clazz : annotatedClasses) {
            ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
            if (componentScan != null) {
                String[] value = componentScan.value();
                if (value == null || value.length == 0) {
                    continue;
                }
                annotatedClasses.addAll(scanner.findAnnotatedClassesList(annotations, componentScan.value()));
                break;
            }
        }

        List<Class<BeanFactoryPostProcessor>> beanFactoryPostProcessorsClassList = new ArrayList<>();
        if (!AnnotationUtils.findAllClassAnnotations(mainClass, EnableAutoConfiguration.class).isEmpty()) {
            List<String> allProcessors = new ArrayList<>(autoConfigurationMap.get("BeanDefinitionRegistryPostProcessor"));
            allProcessors.addAll(autoConfigurationMap.get("BeanFactoryPostProcessor"));
            // 处理所有处理器类，并且检查它们是否是 BeanFactoryPostProcessor 类型的子类
            for (String className : allProcessors) {
                try {
                    Class<?> clazz = Class.forName(className);
                    processImports(annotatedClasses, clazz, beanFactoryPostProcessorsClassList);
                    // 如果是 BeanFactoryPostProcessor，添加到处理器列表
                    if (BeanFactoryPostProcessor.class.isAssignableFrom(clazz)) {
                        beanFactoryPostProcessorsClassList.add((Class<BeanFactoryPostProcessor>) clazz);
                    }
                } catch (ClassNotFoundException e) {
                    log.warn(e.getMessage(), e);
                }
            }

            //处理所有通过配置文件指定的 Bean类
            for (String className : spiMap.get("Beans")) {
                try {
                    Class<?> clazz = Class.forName(className);
                    annotatedClasses.add(clazz);
                    processImports(annotatedClasses, clazz, beanFactoryPostProcessorsClassList);
                } catch (ClassNotFoundException e) {
                    System.err.println("未找到类: " + className);
                }
            }
        }

        //递归处理所有通过@Import注解引入的类
        for (Class<?> clazz : annotatedClasses.toArray(new Class[0])) {
            processImports(annotatedClasses, clazz, beanFactoryPostProcessorsClassList);
        }

        for (Class<?> clazz : annotatedClasses) {
            List<MyConfig> allClassAnnotations = AnnotationUtils.findAllClassAnnotations(clazz, MyConfig.class);
            if (!allClassAnnotations.isEmpty() || FactoryBean.class.isAssignableFrom(clazz)) {
                if (clazz.isAnnotation()) {
                    continue;
                }
                MyBeanDefinition myConfigBeanDefinition = new MyBeanDefinition(clazz.getName(), clazz);
                if (registry.containsBeanDefinition(myConfigBeanDefinition.getName())) {
                    continue;
                }
                registry.registerBeanDefinition(myConfigBeanDefinition.getName(), myConfigBeanDefinition);
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getAnnotation(AutumnBean.class) != null) {
                        //xxx: 当这个方法有bean注解则创建一个MyBeanDefinition
                        MyBeanDefinition myBeanDefinition = getMyBeanDefinition(clazz, method);
                        if (registry.containsBeanDefinition(myConfigBeanDefinition.getName())) {
                            continue;
                        }
                        registry.registerBeanDefinition(myBeanDefinition.getName(), myBeanDefinition);
                    }

                }
                //实际上这是一个完全错误的设计,但确实可以起到作用,马上重写,context里面以及留好方法了
                if (FactoryBean.class.isAssignableFrom(clazz)) {
                    try {
                        Method getObjectMethod = clazz.getMethod("getObject");
                        MyBeanDefinition myBeanDefinition = getMyBeanDefinition(clazz, getObjectMethod);
                        if (!registry.containsBeanDefinition(myConfigBeanDefinition.getName())) {
                            registry.registerBeanDefinition(myBeanDefinition.getName(), myBeanDefinition);
                        }

                    } catch (NoSuchMethodException e) {
                        System.err.println("FactoryBean实现中没有找到getObject方法: " + clazz.getName());
                    }
                }
            } else {
                if (clazz.isAnnotation()) {
                    continue;
                }
                MyBeanDefinition myBeanDefinition = new MyBeanDefinition(clazz);
                if (!registry.containsBeanDefinition(myBeanDefinition.getName())) {
                    registry.registerBeanDefinition(myBeanDefinition.getName(), myBeanDefinition);
                }
            }
        }

    }


    private MyBeanDefinition getMyBeanDefinition(Class clazz, Method method) {
        Class<?> returnType = method.getReturnType();
        if (returnType.equals(void.class)) {
            throw new BeanCreationException("用AutumnBean注解标记的方法返回值不能为void");
        }
        String beanName;
        AutumnBean annotation = method.getAnnotation(AutumnBean.class);
        if (annotation == null && FactoryBean.class.isAssignableFrom(clazz)) {
            beanName = returnType.getName();

        } else {
            if (method.getAnnotation(AutumnBean.class).value().isEmpty()) {
                beanName = returnType.getName();
            } else {
                beanName = method.getAnnotation(AutumnBean.class).value();
            }
        }
        MyBeanDefinition myBeanDefinition = new MyBeanDefinition(beanName, returnType);
        myBeanDefinition.setDoMethod(method);
        myBeanDefinition.setConfigurationClass(clazz);
        return myBeanDefinition;

    }

    @Override
    public void postProcessBeanFactory(AnnotationScanner scanner, BeanDefinitionRegistry registry) throws Exception {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private void initAutumnSpi() {
        try {
            spiMap = AutumnFactoriesLoader.parseConfigurations();
            autoConfigurationMap = AutumnFactoriesLoader.parseAutoConfigurations();
        } catch (Exception e) {
            log.error("加载spi配置文件失败", e);
            throw new RuntimeException(e);
        }
    }

    private void processImports(Set<Class<?>> annotatedClasses, Class<?> clazz, List<Class<BeanFactoryPostProcessor>> beanFactoryPostProcessorsClassList) {
        List<Import> importAnnotations = AnnotationUtils.findAllClassAnnotations(clazz, Import.class);

        if (!importAnnotations.isEmpty()) {
            for (Import importAnnotation : importAnnotations) {
                for (Class<?> importClass : importAnnotation.value()) {
                    if (importClass.isInterface() || importClass.isAnnotation()) {
                        throw new RuntimeException("Import注解不能引入接口或注解: " + importClass.getName());
                    }
                    log.info("通过Import机制导入了{}", importClass.getName());
                    annotatedClasses.add(importClass);

                    if (BeanFactoryPostProcessor.class.isAssignableFrom(importClass)) {
                        beanFactoryPostProcessorsClassList.add((Class<BeanFactoryPostProcessor>) importClass);
                    }

                    processImports(annotatedClasses, importClass, beanFactoryPostProcessorsClassList);
                }
            }
        }
    }

}
