package org.example.FrameworkUtils.Webutils;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Annotation.EnableAop;
import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyConditional;
import org.example.FrameworkUtils.Annotation.MyMapper;
import org.example.FrameworkUtils.Annotation.MyService;
import org.example.FrameworkUtils.Annotation.Value;
import org.example.FrameworkUtils.AopProxyFactory;
import org.example.FrameworkUtils.Jdbcinit;
import org.example.FrameworkUtils.MapperUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wsh
 */
@Slf4j
public class MyContext {
    @FunctionalInterface
    interface ObjectFactory<T> {
        T getObject() throws Exception;
    }

    private static volatile MyContext instance;

    private MyContext() {
    }

    public static MyContext getInstance() {
        if (instance == null) {
            synchronized (MyContext.class) {
                if (instance == null) {
                    instance = new MyContext();
                }
            }
        }
        return instance;
    }

    private Map<String, Object> sharedMap = new HashMap<>();
    private Set<Class<?>> iocContainer;
    //xxx:一级缓存存储成熟bean
    private final Map<Class<?>, Object> singletonObjects = new ConcurrentHashMap<>();

    //xxx: 二级缓存提前暴露的还未完全成熟的bean,用于解决循环依赖
    private final Map<Class<?>, Object> earlySingletonObjects = new ConcurrentHashMap<>();

    //xxx: 三级缓存：对象工厂,创建代理/普通bean
    private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>();
    private Jdbcinit jdbcinit = new Jdbcinit();
    private Properties properties = jdbcinit.initProperties();

    public Object getBean(Class<?> beanClass) {
        //xxx:寻找一级缓存
        Object singletonObject = singletonObjects.get(beanClass);
        //xxx:一级缓存找不到
        if (singletonObject == null) {
            //xxx:二级缓存寻找
            singletonObject = earlySingletonObjects.get(beanClass);
            //xxx:二级缓存中找不到
            if (singletonObject == null) {
                //xxx: 从三级缓存获取工厂方法
                ObjectFactory<?> singletonFactory = singletonFactories.get(beanClass.getName());
                if (singletonFactory != null) {
                    try {
                        singletonObject = singletonFactory.getObject();
                        earlySingletonObjects.put(beanClass, singletonObject);
                        singletonFactories.remove(beanClass.getName());
                    } catch (Exception e) {
                        throw new RuntimeException("创建Bean实例失败: " + beanClass.getName(), e);
                    }
                }
            }
        }

        return singletonObject;
    }

    //xxx:初始化第三缓存
    public void initIocCache(Set<Class<?>> prototypeIocContainer) throws NoSuchFieldException, IllegalAccessException {
        this.iocContainer = prototypeIocContainer;
        registerBeanDefinition(this.iocContainer);
        log.info(singletonFactories.toString());
        for (Class<?> clazz : this.iocContainer) {
            initBean(clazz);
        }
    }

    private void initBean(Class<?> clazz) throws NoSuchFieldException, IllegalAccessException {
        Object bean = getBean(clazz);
        if (bean != null) {
            autowireBeanProperties(bean);
        }

    }

    //xxx:遍历set去填充第三缓存
    public void registerBeanDefinition(Set<Class<?>> beanDefinitionMap) {
        for (Class<?> beanClass : beanDefinitionMap) {
            ObjectFactory<?> beanFactory = createBeanFactory(beanClass);
            singletonFactories.put(beanClass.getName(), beanFactory);
        }
    }
    //xxx:判断使用哪种工厂
    private ObjectFactory<?> createBeanFactory(Class<?> beanClass) {
        if (beanClass.getDeclaredAnnotation(MyMapper.class) != null) {
            return () -> createMapperBeanInstance(beanClass);
        } else if (beanClass.getAnnotation(EnableAop.class) != null) {
            return () -> createAopBeanInstance(beanClass);
        } else {
            return () -> createBeanInstance(beanClass);
        }

    }

    //xxx:Aop工厂
    private Object createAopBeanInstance(Class<?> beanClass) {
        System.out.println("cglibbean");
        AopProxyFactory aopProxyFactory=new AopProxyFactory();
        try {
            return aopProxyFactory.create(beanClass);
        } catch (Exception e) {
            throw new RuntimeException("创建cglibbean实例失败", e);
        }
    }


    //xxx:普通bean工厂
    private Object createBeanInstance(Class<?> beanClass) {
        System.out.println("普通bean");
        try {
            return beanClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("创建普通bean实例失败", e);
        }
    }

    //xxx:MapperBean工厂
    private Object createMapperBeanInstance(Class<?> beanClass) {
        System.out.println("MapperBean动态代理Bean");
        try {
            return MapperUtils.init(beanClass);
        } catch (Exception e) {
            throw new RuntimeException("创建MapperBean实例失败", e);
        }
    }

    //xxx:注入器,递归深层注入/循环引用
    public void autowireBeanProperties(Object bean) throws NoSuchFieldException, IllegalAccessException {
        Class<?> beanClass = bean.getClass();
        if(beanClass.getName().contains("$$")){
            Class parentClass = bean.getClass().getSuperclass();
            for (Field field : parentClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(MyAutoWired.class)){
                    injectValueAnnotationByAutowired(field,bean,beanClass);
                }else if (field.isAnnotationPresent(Value.class)){
                    injectValueAnnotation(bean, field);
                }
            }
        }
        for (Field field : beanClass.getDeclaredFields()) {

            if (field.isAnnotationPresent(MyAutoWired.class)) {
                if (field.getType().isInterface()) {
                    if (field.getType().getAnnotation(MyMapper.class) == null) {
                        String packageName = (String) get("packageUrl");
                        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
                        Set<Class<?>> subTypesOf = (Set<Class<?>>) reflections.getSubTypesOf(field.getType());
                        if (subTypesOf.size() > 1) {
                            if(subTypesOf.size()==2){
                                Iterator<Class<?>> it = subTypesOf.iterator();
                                while (it.hasNext()){
                                    Class nextclass = it.next();
                                    if(nextclass.getAnnotation(MyService.class)!=null && nextclass.getAnnotation(MyConditional.class)==null) {
                                        Object dependency = getBean(nextclass);
                                        if (dependency == null) {
                                            throw new RuntimeException("无法解析的依赖：" + nextclass);
                                        }
                                        field.setAccessible(true);
                                        try {
                                            field.set(bean, dependency);
                                        } catch (IllegalAccessException e) {
                                            throw new RuntimeException("无法注入依赖：" + field.getName(), e);
                                        }
                                        singletonObjects.put(beanClass, bean);
                                        earlySingletonObjects.remove(beanClass);
                                        return;
                                    }
                                }
                            }
                            else {
                                throw new RuntimeException("多个实现类" + subTypesOf);
                            }

                        }
                        Class<?> firstImplementation = null;
                        if (!subTypesOf.isEmpty()) {
                            firstImplementation = subTypesOf.iterator().next();
                        }
                        Class<?> dependencyType = firstImplementation;
                        Object dependency = getBean(dependencyType);
                        if (dependency == null) {
                            throw new RuntimeException("无法解析的依赖：" + dependencyType.getName());
                        }
                        field.setAccessible(true);
                        try {
                            field.set(bean, dependency);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("无法注入依赖：" + field.getName(), e);
                        }
                        singletonObjects.put(beanClass, bean);
                        earlySingletonObjects.remove(beanClass);
                        return;
                        }

                    }

                Class<?> dependencyType = field.getType();
                Object dependency = getBean(dependencyType);
                if (dependency == null) {
                    throw new RuntimeException("无法解析的依赖：" + dependencyType.getName());
                }
                field.setAccessible(true);
                try {
                    field.set(bean, dependency);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("无法注入依赖：" + field.getName(), e);
                }

            } else if (field.isAnnotationPresent(Value.class)) {
                injectValueAnnotation(bean, field);
            }
        }
        singletonObjects.put(beanClass, bean);
        earlySingletonObjects.remove(beanClass);
    }

    private void injectValueAnnotationByAutowired(Field field,Object bean,Class<?> beanClass){
        if (field.getType().isInterface()) {
            if (field.getType().getAnnotation(MyMapper.class) == null) {
                String packageName = (String) get("packageUrl");
                Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
                Set<Class<?>> subTypesOf = (Set<Class<?>>) reflections.getSubTypesOf(field.getType());
                if(subTypesOf.size()>1){
                    throw new RuntimeException("多个实现类" + subTypesOf);
                }
                Class<?> firstImplementation = null;
                if (!subTypesOf.isEmpty()) {
                    firstImplementation = subTypesOf.iterator().next();
                }
                Class<?> dependencyType = firstImplementation;
                Object dependency = getBean(dependencyType);
                if (dependency == null) {
                    throw new RuntimeException("无法解析的依赖：" + dependencyType.getName());
                }
                field.setAccessible(true);
                try {
                    field.set(bean, dependency);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("无法注入依赖：" + field.getName(), e);
                }
                singletonObjects.put(beanClass, bean);
                earlySingletonObjects.remove(beanClass);
                return ;
            }
        }
        Class<?> dependencyType = field.getType();
        Object dependency = getBean(dependencyType);
        if (dependency == null) {
            throw new RuntimeException("无法解析的依赖：" + dependencyType.getName());
        }
        field.setAccessible(true);
        try {
            field.set(bean, dependency);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("无法注入依赖：" + field.getName(), e);
        }
    }

    private void injectValueAnnotation(Object instance, Field field) throws NoSuchFieldException {
        Value value = field.getAnnotation(Value.class);
        if (value == null || "".equals(value.value())) {
            log.error("没有传递内容,注入失败");
            return;
        }

        String propertyValue = properties.getProperty(value.value());
        if (propertyValue == null) {
            log.error("属性未找到,注入失败");
            return;
        }

        try {
            field.setAccessible(true);
            Object convertedValue = convertStringToType(propertyValue, field.getType());
            field.set(instance, convertedValue);
            log.warn("注入配置文件  @value, key: " + value.value() + "  value: " + propertyValue);
        } catch (Exception e) {
            log.error("依赖注入失败：" + e.getMessage());
        }
        }



    private Object convertStringToType(String value, Class<?> type) {
        if (Integer.TYPE.equals(type) || Integer.class.equals(type)) {
            return Integer.parseInt(value);
        } else if (Float.TYPE.equals(type) || Float.class.equals(type)) {
            return Float.parseFloat(value);
        } else if (String.class.equals(type)) {
            return value;
        }
        throw new IllegalArgumentException("不支持的类型: " + type);
    }

    public void put(String key, Object value) {
        sharedMap.put(key, value);
    }

    public Object get(String key) {
        return sharedMap.get(key);
    }

    public Map<Class<?>, Object> getIocContainer() {
        return singletonObjects;
    }
}
