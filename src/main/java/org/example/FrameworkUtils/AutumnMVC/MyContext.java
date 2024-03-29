package org.example.FrameworkUtils.AutumnMVC;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnMVC.Annotation.EnableAop;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnMVC.Annotation.Value;
import org.example.FrameworkUtils.Exception.BeanCreationException;
import org.example.FrameworkUtils.Orm.MineBatis.Jdbcinit;
import org.example.FrameworkUtils.Orm.MineBatis.MapperUtils;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MyMapper;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    private final AopProxyFactory aopProxyFactory= new AopProxyFactory();
    private final Map<String, Object> sharedMap = new ConcurrentHashMap<>();

    private final Map<String, MyBeanDefinition> beanDefinitionMaps = new ConcurrentHashMap<>();
    //xxx:一级缓存存储成熟bean
    private final Map<Class<?>, MyBeanDefinition> singletonObjects = new ConcurrentHashMap<>();

    //xxx: 二级缓存提前暴露的还未完全成熟的bean,用于解决循环依赖
    private final Map<Class<?>, MyBeanDefinition> earlySingletonObjects = new ConcurrentHashMap<>();

    //xxx: 三级缓存：对象工厂,创建Jdk代理/CgLib代理/配置类Bean/普通Bean
    private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>();
    private final Jdbcinit jdbcinit = new Jdbcinit();
    private final Properties properties = jdbcinit.initProperties();

    private List<Class<?>> cycleSet=new ArrayList<>();
    private final Map<String,List<Class<?>>> beanDependencies=new HashMap<>();
    int times=1;


    public <T> T getBean(Class<T> beanClass) {
        if (times != 1) {
            cycleSet.add(beanClass);
        }
        times++;

        // xxx:寻找一级缓存
        MyBeanDefinition singletonDefinition = singletonObjects.get(beanClass);
        Object singletonObject = (singletonDefinition != null) ? singletonDefinition.getInstance() : null;

        // xxx:一级缓存找不到
        if (singletonObject == null) {
            // xxx:二级缓存寻找
            MyBeanDefinition earlySingleton = earlySingletonObjects.get(beanClass);
            if (earlySingleton != null) {
                singletonObject = earlySingleton.getInstance();
            } else {
                // xxx:二级缓存中找不到
                // xxx: 从三级缓存获取工厂方法
                ObjectFactory<?> singletonFactory = singletonFactories.get(beanClass.getName());
                if (singletonFactory != null) {
                    try {
                        singletonObject = singletonFactory.getObject();
                        // xxx:生产对象后从第三级移除,进入第二级缓存
                        MyBeanDefinition mb = new MyBeanDefinition(beanClass.getName(), beanClass);
                        mb.setInstance(singletonObject);
                        earlySingletonObjects.put(beanClass, mb);
                        singletonFactories.remove(beanClass.getName());
                    } catch (Exception e) {
                        throw new BeanCreationException("创建Bean实例失败: " + beanClass.getName(), e);
                    }
                }
            }
        }
        return (T) singletonObject;
    }


    //xxx:初始化第三缓存
    public void initIocCache(Set<Class<?>> prototypeIocContainer) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        for(Class<?> clazz:prototypeIocContainer){
            beanDefinitionMaps.put(clazz.getName(),new MyBeanDefinition(clazz.getName(),clazz));
        }
        //xxx:填充第三级缓存
        registerBeanDefinition(beanDefinitionMaps);
        //xxx:缓存添加好后,遍历外界传递的Set,对Bean进行初始化
        for (Class<?> clazz : prototypeIocContainer) {
            cycleSet = new ArrayList<>();
            initBean(clazz);
            if (!cycleSet.isEmpty()) {
                beanDependencies.put(clazz.getName(), new ArrayList<>(cycleSet));
            }
            times=1;
        }
        DependencyChecker dependencyChecker=getBean(DependencyChecker.class);
        dependencyChecker.checkForCyclicDependencies(beanDependencies);

    }

    private void initBean(Class<?> clazz) throws NoSuchFieldException, IllegalAccessException {
        Object bean = getBean(clazz);
        if (bean != null) {
            //xxx:依赖注入开始
            autowireBeanProperties(bean);
        }


    }

    //xxx:遍历set去填充第三缓存
    public void registerBeanDefinition(Map<String,MyBeanDefinition>  beanDefinitionMaps) {
        for (MyBeanDefinition beanDefinition : beanDefinitionMaps.values()) {
            ObjectFactory<?> beanFactory = createBeanFactory(beanDefinition.getBeanClass());
            singletonFactories.put(beanDefinition.getName(), beanFactory);
            //xxx:查找带@AutunmnBean的字段,生成工厂放入第三缓存
            if (beanDefinition.getBeanClass().getAnnotation(MyConfig.class) != null) {
                Method[] methods = beanDefinition.getBeanClass().getDeclaredMethods();
                for (Method method : methods) {
                    AutumnBean annotation = method.getAnnotation(AutumnBean.class);
                    if (annotation != null) {
                        if(!annotation.value().isEmpty()){
                            singletonFactories.put(annotation.value(), createBeanFactory(beanDefinition.getBeanClass(),method));
                        }else{
                            singletonFactories.put(method.getReturnType().getName(), createBeanFactory(beanDefinition.getBeanClass(),method));
                        }

                    }
                }
            }
        }
    }

    private Object createAutumnBeanInstance(Class<?> beanClass, Method method) {
        try {
            //xxx:第三级缓存拿到自己的原始对象
            Object magicBean=getBean(beanClass);
            //xxx:对自己进行依赖注入
            autowireBeanProperties(magicBean);
            //xxx:用成熟的自己来反射执行方法
            return method.invoke(magicBean);
        } catch (Exception e) {
            throw new BeanCreationException("创建@bean标注的实例失败,请检查你是否存在一个有参构造器,有的话创建一个无参构造器", e);
        }
    }
    //xxx:判断使用哪种工厂
    private ObjectFactory<?> createBeanFactory(Class<?> beanClass) {
        if (beanClass.getDeclaredAnnotation(MyMapper.class) != null) {
            return () -> createMapperBeanInstance(beanClass);
        } else if (beanClass.getAnnotation(EnableAop.class) != null) {
            return () -> createAopBeanInstance(beanClass);
        }  else {
            return () -> createBeanInstance(beanClass);
        }

    }

    //xxx:判断器重载
    private ObjectFactory<?> createBeanFactory(Class<?> beanClass, Method method) {
        return () -> createAutumnBeanInstance(beanClass,method);
    }


    //xxx:Aop工厂
    private Object createAopBeanInstance(Class<?> beanClass) {
        String[] methods=beanClass.getAnnotation(EnableAop.class).getMethod();
        Class<?> clazz = beanClass.getAnnotation(EnableAop.class).getClassFactory();
        if(clazz==null || methods==null || methods.length==0){
            throw new IllegalArgumentException("检查Aop注解参数是否加全了");
        }
        try{
            return aopProxyFactory.create(clazz ,beanClass,methods);
        }catch (Exception e){
            throw new BeanCreationException("解析注解错误,保证Aop配置类可以被实例化\n创建CgLibBean实例失败", e);
        }

    }


    //xxx:普通bean工厂
    private Object createBeanInstance(Class<?> beanClass) {
        try {
            return beanClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new BeanCreationException("创建普通bean实例失败,请检查你是否存在一个有参构造器,有的话创建一个无参构造器", e);
        }
    }

    //xxx:MapperBean工厂
    private Object createMapperBeanInstance(Class<?> beanClass) {
        try {
            return MapperUtils.init(beanClass);
        } catch (Exception e) {
            throw new BeanCreationException("创建MapperBean实例失败", e);
        }
    }

    //xxx:di部分:

    public void autowireBeanProperties(Object bean) throws IllegalAccessException, NoSuchFieldException {
        //xxx:是否为CgLib代理类?,是的话因为子类不能继承父类字段等注解,需要去父类身上查找
        Class<?> clazz = bean.getClass().getName().contains("$$")
                ? bean.getClass().getSuperclass() : bean.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            //xxx:标记@auto wired进行对象/接口依赖注入
            if (field.isAnnotationPresent(MyAutoWired.class)) {
                injectDependencies(bean, field);
            }
            //xxx:进行配置文件注入
            else if (field.isAnnotationPresent(Value.class)) {
                injectValueAnnotation(bean, field);
            }

        }
        //xxx:依赖注入后更新缓存,这个bean从第二缓存移除,进入一级缓存,提前暴露期转为成熟的AutumnBean
        MyBeanDefinition mb=new MyBeanDefinition(bean.getClass().getName(), bean.getClass());
        mb.setInstance(bean);
        singletonObjects.put(bean.getClass(), mb);
        earlySingletonObjects.remove(bean.getClass());
    }

    private void injectDependencies(Object bean, Field field) throws IllegalAccessException, NoSuchFieldException {
        Class<?> fieldType = field.getType();
        //xxx:接口,还是不是mapper
        if (fieldType.isInterface() && fieldType.getAnnotation(MyMapper.class) == null) {
            //xxx:进入查找实现类环节
            injectInterfaceTypeDependency(bean, field);
        } else {
            //xxx:正常的Bean
            injectNormalDependency(bean, field);
        }

    }


    private void injectInterfaceTypeDependency(Object bean, Field field) throws IllegalAccessException, NoSuchFieldException {
        String packageName = (String) get("packageUrl");
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        Set<Class<?>> subTypesOf = new HashSet<>(reflections.getSubTypesOf(field.getType()));
        Set<Class<?>> classesToRemove = new HashSet<>();
        for (Class<?> implClass : subTypesOf) {
            MyConditional myConditionalAnnotation = implClass.getAnnotation(MyConditional.class);
            if (myConditionalAnnotation != null) {
                Class<? extends MyCondition> conditionClass = myConditionalAnnotation.value();
                MyCondition condition = getBean(conditionClass);
                autowireBeanProperties(condition);
                condition.init();
                if (!condition.matches(getInstance(),implClass)) {
                    classesToRemove.add(implClass);
                }
                condition.after();
            }
        }
        subTypesOf.removeAll(classesToRemove);
        Class<?> selectedImpl = null;
        if (subTypesOf.size() == 1) {
            selectedImpl = subTypesOf.iterator().next();
        } else if (subTypesOf.size() > 1) {
            log.error("多个实现类均命中,请添加合理的条件注解来进行选择性注入,冲突的实现类如下:\n"+subTypesOf);
            throw new BeanCreationException("多个实现类命中");
        }
        if (selectedImpl != null) {
            Object dependency = getBean(selectedImpl);
            field.setAccessible(true);
            field.set(bean, dependency);
        } else {
            throw new BeanCreationException("无法解析的依赖：" + field.getType());
        }
    }


    //xxx:注入一般Bean,依照字段查找类,从容器取出为字段赋值
    private void injectNormalDependency(Object bean, Field field) throws IllegalAccessException, NoSuchFieldException {
        Class<?> dependencyType = field.getType();
        Object dependency = getBean(dependencyType);
        if (dependency == null) {
            log.warn("无法解析的依赖：" + dependencyType.getName());
            return;
        }
        field.setAccessible(true);
        if(dependency.getClass().equals(MyBeanDefinition.class)){
            field.set(bean, (MyBeanDefinition)((MyBeanDefinition) dependency).getInstance());
        }else{
            field.set(bean, dependency);
        }

    }

    //xxx:注入配置文件
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
        } catch (Exception e) {
            log.error("依赖注入失败：" + e.getMessage());
        }
        }


    //xxx:配置文件编码器
    private Object convertStringToType(String value, Class<?> type) {
        if (Integer.TYPE.equals(type) || Integer.class.equals(type)) {
            return Integer.parseInt(value);
        } else if (Float.TYPE.equals(type) || Float.class.equals(type)) {
            return Float.parseFloat(value);
        } else if (Double.TYPE.equals(type) || Double.class.equals(type)) {
            return Double.parseDouble(value);
        } else if (Long.TYPE.equals(type) || Long.class.equals(type)) {
            return Long.parseLong(value);
        } else if (Boolean.TYPE.equals(type) || Boolean.class.equals(type)) {
            return Boolean.parseBoolean(value);
        } else if (Byte.TYPE.equals(type) || Byte.class.equals(type)) {
            return Byte.parseByte(value);
        } else if (Short.TYPE.equals(type) || Short.class.equals(type)) {
            return Short.parseShort(value);
        } else if (Character.TYPE.equals(type) || Character.class.equals(type)) {
            if (value.length() == 1) {
                return value.charAt(0);
            } else {
                throw new IllegalArgumentException("Cannot convert String to Character: \"" + value + "\" is not a single character");
            }
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

    public Map<Class<?>, MyBeanDefinition> getIocContainer() {
        return singletonObjects;
    }

}
