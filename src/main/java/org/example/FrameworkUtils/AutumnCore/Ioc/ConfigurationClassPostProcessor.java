package org.example.FrameworkUtils.AutumnCore.Ioc;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.ComponentScan;
import org.example.FrameworkUtils.AutumnCore.Annotation.EnableAutoConfiguration;
import org.example.FrameworkUtils.AutumnCore.Annotation.Import;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyOrder;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotatedBeanDefinitionReader;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationMetadata;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AutumnFactoriesLoader;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.env.Environment;
import org.example.FrameworkUtils.Exception.BeanCreationException;
import org.example.FrameworkUtils.Utils.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    private AnnotationScanner scanner;

    private ConditionEvaluator conditionEvaluator;

    private AnnotatedBeanDefinitionReader annotatedBeanDefinitionReader;

    private String mainPackage;

    private String mainClass;


    @Override
    public void postProcessBeanDefinitionRegistry(AnnotationScanner scanner, BeanDefinitionRegistry registry) throws Exception {
        initAutumnSpi();
        this.scanner = scanner;
        this.conditionEvaluator = new ConditionEvaluator(registry);
        this.annotatedBeanDefinitionReader = new AnnotatedBeanDefinitionReader(registry);
        Set<Class<?>> annotatedClasses = scanner.findDefaultAnnotatedClassesList(mainPackage);
        Set<String> mainPackageCache = new HashSet<>();
        mainPackageCache.add(mainPackage);
        processComponentScan(annotatedClasses, mainPackageCache);
        Set<Class<?>> cache = new HashSet<>();


        if (!AnnotationUtils.findAnnotation(mainClass, EnableAutoConfiguration.class).isEmpty()) {
            List<String> allProcessors = new ArrayList<>(autoConfigurationMap.get("BeanDefinitionRegistryPostProcessor"));
            allProcessors.addAll(autoConfigurationMap.get("BeanFactoryPostProcessor"));
            for (String className : allProcessors) {
                try {
                    Class<?> clazz = Class.forName(className);
                    processImports(annotatedClasses, clazz, cache);
                } catch (ClassNotFoundException e) {
                    log.error(e.getMessage(), e);
                }
            }

            for (String className : spiMap.get("Beans")) {
                try {
                    Class<?> clazz = Class.forName(className);
                    annotatedClasses.add(clazz);
                    processImports(annotatedClasses, clazz, cache);
                } catch (ClassNotFoundException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        //递归处理所有通过@Import注解引入的类
        for (Class<?> clazz : annotatedClasses.toArray(new Class[0])) {
            processImports(annotatedClasses, clazz, cache);
        }

        for (Class<?> clazz : annotatedClasses) {
            Optional<MyConfig> configOptional = AnnotationUtils.findAnnotation(clazz, MyConfig.class);
            if (configOptional.isPresent()) {
                if (clazz.isAnnotation()) {
                    continue;
                }
                String configName = AnnotationUtils.findBeanName(clazz);
                annotatedBeanDefinitionReader.register(configName, clazz);
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getAnnotation(AutumnBean.class) != null) {
                        MyBeanDefinition myBeanDefinition = createAutumnBeanDefinition(clazz, method);
                        registry.registerBeanDefinition(myBeanDefinition.getName(), myBeanDefinition);
                    }
                }
            } else {
                if (clazz.isAnnotation()) {
                    continue;
                }
                annotatedBeanDefinitionReader.register(AnnotationUtils.findBeanName(clazz), clazz);
            }

        }


    }

    // 使用递归的方式来扫描新的包路径
    public void processComponentScan(Set<Class<?>> annotatedClasses, Set<String> scannedPackages) {
        Set<Class<?>> newClassesToAdd = new HashSet<>();
        Set<String> newScannedPackages = new HashSet<>();

        for (Class<?> clazz : annotatedClasses) {
            AnnotationMetadata annotationMetadata = new AnnotationMetadata(clazz);
            if (!conditionEvaluator.shouldSkip(annotationMetadata)) {
                Optional<ComponentScan> annotated = annotationMetadata.isAnnotated(ComponentScan.class);
                if (annotated.isPresent()) {
                    String[] value = annotated.get().value();
                    if (value == null || value.length == 0) {
                        continue;
                    }
                    log.info("通过ComponentScan注解扫描包:{}", (Object) annotated.get().value());
                    for (String packageToScan : value) {
                        if (!scannedPackages.contains(packageToScan)) {
                            Set<Class<?>> foundClasses = scanner.findDefaultAnnotatedClassesList(packageToScan);
                            newClassesToAdd.addAll(foundClasses);
                            newScannedPackages.add(packageToScan);
                        }
                    }
                }
            }
        }
        if (!newClassesToAdd.isEmpty()) {
            annotatedClasses.addAll(newClassesToAdd);
            scannedPackages.addAll(newScannedPackages);
            processComponentScan(newClassesToAdd, scannedPackages);
        }

    }


    private MyBeanDefinition createAutumnBeanDefinition(Class clazz, Method method) {
        Class<?> returnType = method.getReturnType();
        if (returnType.equals(void.class)) {
            throw new BeanCreationException("用AutumnBean注解标记的方法返回值不能为void");
        }
        String beanName;
        AutumnBean annotation = method.getAnnotation(AutumnBean.class);
        String initMethodName = annotation.initMethod();
        String destroyMethodName = annotation.destroyMethod();

        if (annotation.value().isEmpty()) {
            beanName = returnType.getName();
        } else {
            beanName = annotation.value();
        }


        MyBeanDefinition myBeanDefinition = new MyBeanDefinition(beanName, returnType);
        for (Method m : myBeanDefinition.getBeanClass().getDeclaredMethods()) {
            m.setAccessible(true);
            if (m.getName().equals(initMethodName)) {
                myBeanDefinition.getInitMethodName().add(initMethodName);
                myBeanDefinition.getInitMethod().add(m);
            }
            if (m.getName().equals(destroyMethodName)) {
                myBeanDefinition.getAfterMethodName().add(destroyMethodName);
                myBeanDefinition.getAfterMethod().add(m);
            }
        }
        myBeanDefinition.setDoMethod(method);
        myBeanDefinition.setConfigurationClass(clazz);
        return myBeanDefinition;

    }

    @Override
    public void postProcessBeanFactory(AnnotationScanner scanner, BeanDefinitionRegistry registry) throws Exception {

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

    private void processImports(Set<Class<?>> annotatedClasses, Class<?> clazz, Set<Class<?>> cacache) {

        if (cacache.add(clazz)) {
            if (clazz.isAnnotation()) {
                return;
            }
            annotatedBeanDefinitionReader.register(AnnotationUtils.findBeanName(clazz), clazz);
            annotatedClasses.add(clazz);
            List<Import> importAnnotations = AnnotationUtils.findAnnotations(clazz, Import.class);
            for (Import importAnnotation : importAnnotations) {
                if (importAnnotation.value() != null) {
                    for (Class<?> importClass : importAnnotation.value()) {
                        if (importClass.isInterface() || importClass.isAnnotation()) {
                            throw new RuntimeException("Import注解不能引入接口或注解: " + importClass.getName());
                        }

                        if (!annotatedClasses.contains(importClass)) {
                            log.info("通过Import机制导入了{}", importClass.getName());
                            // 递归导入
                            processImports(annotatedClasses, importClass, cacache);
                        }
                    }
                }
            }

        }


    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        mainPackage = environment.getProperty("autumn.main.package");
        mainClass = environment.getProperty("autumn.main.sources");
    }


}
