package org.example.FrameworkUtils.AutumnMVC.Ioc;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.EnableAop;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnMVC.Annotation.Value;
import org.example.FrameworkUtils.AutumnMVC.Aop.AopProxyFactory;
import org.example.FrameworkUtils.AutumnMVC.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnMVC.BeanLoader.ObjectFactory;
import org.example.FrameworkUtils.Exception.BeanCreationException;
import org.example.FrameworkUtils.Orm.MineBatis.Jdbcinit;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MyMapper;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

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

    //xxx:一级缓存存储成熟bean
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    //xxx: 二级缓存提前暴露的还未完全成熟的bean,用于解决循环依赖
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();

    //xxx: 三级缓存：对象工厂,创建Jdk代理/CgLib代理/配置类Bean/普通Bean
    private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>();
    private final Jdbcinit jdbcinit = new Jdbcinit();
    private final Properties properties = jdbcinit.initProperties();

    private List<Class<?>> cycleSet=new ArrayList<>();
    private final Map<String,List<Class<?>>> beanDependencies=new HashMap<>();
    private int times=1;


    public Object getBean(String beanName) {
        try {
            if (times != 1) {
                cycleSet.add(Class.forName(beanName));
            }
            times++;
        } catch (ClassNotFoundException e) {
        }

        // xxx:寻找一级缓存
        Object singletonObject = singletonObjects.get(beanName);

        // xxx:一级缓存找不到
        if (singletonObject == null) {
            // xxx:二级缓存寻找
            singletonObject = earlySingletonObjects.get(beanName);
            if (singletonObject == null) {
                // xxx:二级缓存中找不到
                // xxx: 从三级缓存获取工厂方法
                ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    try {
                        singletonObject = singletonFactory.getObject();
                        //xxx:生产对象后更新缓存
                        earlySingletonObjects.put(beanName, singletonObject);
                        singletonFactories.remove(beanName);
                    } catch (Exception e) {
                        log.error(String.valueOf(e));
                        throw new BeanCreationException("创建Bean实例失败: " + beanName, e);
                    }
                }
            }
            if (singletonObject != null) {
                //xxx:更新一级缓存
                singletonObjects.put(beanName, singletonObject);
                //xxx:从二级缓存移除
                earlySingletonObjects.remove(beanName);
            }
        }
        return singletonObject;
    }


    public void initIocCache(Map<String, MyBeanDefinition> beanDefinitionMap) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {

        registerBeanDefinition(beanDefinitionMap);
        //xxx:缓存添加好后，遍历外界传递的 Map 的 values对每个 Bean 进行初始化
        for (MyBeanDefinition definition : beanDefinitionMap.values()) {
            cycleSet = new ArrayList<>();
            initBean(definition);
            if (!cycleSet.isEmpty()) {
                beanDependencies.put(definition.getName(), new ArrayList<>(cycleSet));
            }
            times = 1;
        }
        DependencyChecker dependencyChecker = (DependencyChecker) getBean(DependencyChecker.class.getName());
        dependencyChecker.checkForCyclicDependencies(beanDependencies);
    }


    private void initBean(MyBeanDefinition myBeanDefinition) throws NoSuchFieldException, IllegalAccessException {
        //xxx:这时候第三级缓存已经存满了factory
        //xxx:从第三缓存删掉自己,拿到工厂把自己生出来,放入第二缓存
        Object bean = getBean(myBeanDefinition.getName());
        if (bean != null) {
            //xxx:对未成熟bean进行依赖注入
            autowireBeanProperties(bean, myBeanDefinition);
        } else {
            log.error("Bean为空");
        }


    }

    //xxx:遍历set去填充第三缓存
    private void registerBeanDefinition(Map<String, MyBeanDefinition> beanDefinitionMaps) {
        for (MyBeanDefinition beanDefinition : beanDefinitionMaps.values()) {
            //xxx:先看看用哪个工厂
            ObjectFactory<?> beanFactory = createBeanFactory(beanDefinition);
            //xxx:然后狠狠的塞到第三级缓存中
            singletonFactories.put(beanDefinition.getName(), beanFactory);
        }
    }


    private Object createAutumnBeanInstance(MyBeanDefinition mb) {
        try {
            Object configInstance = getBean(mb.getConfigurationClass().getName());
            //xxx:对配置类进行依赖注入,得到成熟的bean
            autowireBeanProperties(configInstance, mb);
            //xxx:获取mb定义的生产方法
            Method beanMethod = mb.getDoMethod();
            Object autumnBean = beanMethod.invoke(configInstance);
            Method initMethod = mb.getInitMethod();
            if (initMethod != null) {
                initMethod.invoke(autumnBean);
            }
            return autumnBean;

        } catch (Exception e) {
            log.error(String.valueOf(e));

            throw new BeanCreationException("创建@bean标注的实例失败,请检查你是否存在一个有参构造器,有的话创建一个无参构造器", e);
        }
    }

    //xxx:判断使用哪种工厂
    private ObjectFactory<?> createBeanFactory(MyBeanDefinition mb) {
        //xxx:第三方实现
        if (mb.isStarter()) {
            return () -> createStarterBeanInstance(mb);
        }
        //xxx:aop工厂,利用cglib生产代理类
        else if (mb.isCglib()) {
            return () -> createAopBeanInstance(mb.getBeanClass());
        } else if (mb.getDoMethod() != null) {
            return () -> createAutumnBeanInstance(mb);
        } else {
            return () -> createBeanInstance(mb.getBeanClass());
        }

    }

    private Object createStarterBeanInstance(MyBeanDefinition mb) {
        try {
            ObjectFactory<?> factory = mb.getStarterMethod();
            if (factory == null) {
                throw new IllegalStateException("没有为 " + mb.getBeanClass().getName() + " 定义工厂方法");
            }
            Object beanInstance = factory.getObject();

            if (mb.getInitMethod() != null) {
                mb.getInitMethod().invoke(beanInstance);
            }
            return beanInstance;

        } catch (Exception e) {
            log.error("创建Bean实例失败: " + mb.getName(), e);
            throw new BeanCreationException("创建Bean实例失败: " + mb.getName(), e);
        }
    }


    //xxx:Aop工厂
    private Object createAopBeanInstance(Class<?> beanClass) {
        String[] methods=beanClass.getAnnotation(EnableAop.class).getMethod();
        Class<?> clazz = beanClass.getAnnotation(EnableAop.class).getClassFactory();
        try{
            return aopProxyFactory.create(clazz ,beanClass,methods);
        }catch (Exception e){
            throw new BeanCreationException("解析注解错误,保证Aop配置类可以被实例化\n创建CgLibBean实例失败", e);
        }

    }

    //xxx:普通bean工厂
    private Object createBeanInstance(Class<?> beanClass) {
        try {
            //xxx:调用工厂生产一个朴实无华童叟无欺的小bean出来
            return beanClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new BeanCreationException("创建普通bean实例失败,请检查你是否存在一个有参构造器,有的话创建一个无参构造器", e);
        }
    }

    //xxx:di部分:
    private void autowireBeanProperties(Object bean, MyBeanDefinition mb) throws IllegalAccessException, NoSuchFieldException {
        //xxx:是否为CgLib代理类?,是的话因为子类不能继承父类字段等注解,需要去父类身上查找
        Class<?> clazz = bean.getClass().getName().contains("$$")
                ? bean.getClass().getSuperclass() : bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            //xxx:标记@auto wired进行对象/接口依赖注入
            if (field.isAnnotationPresent(MyAutoWired.class)) {
                String myAutoWired = field.getAnnotation(MyAutoWired.class).value();
                if (myAutoWired.isEmpty()) {
                    injectDependencies(bean, field, mb);
                } else {
                    Object dependency = getBean(myAutoWired);
                    if (dependency == null) {
                        log.warn("无法解析的依赖：{}", myAutoWired);
                        return;
                    }
                    field.setAccessible(true);
                    field.set(bean, dependency);
                }

            }
            //xxx:进行配置文件注入
            else if (field.isAnnotationPresent(Value.class)) {
                injectValueAnnotation(bean, field);
            }

        }
        //xxx:依赖注入后更新缓存,这个bean从第二缓存移除,进入一级缓存,提前暴露期转为成熟的AutumnBean
        mb.setInstance(bean);
        singletonObjects.put(bean.getClass().getName(), bean);
        earlySingletonObjects.remove(bean.getClass().getName());
    }

    private void injectDependencies(Object bean, Field field, MyBeanDefinition mb) throws IllegalAccessException, NoSuchFieldException {
        Class<?> fieldType = field.getType();
        //xxx:接口,还是不是mapper,那一定是service
        if (fieldType.isInterface() && fieldType.getAnnotation(MyMapper.class) == null) {
            //xxx:进入查找实现类环节
            injectInterfaceTypeDependency(bean, field, mb);
        } else {
            //xxx:正常的Bean
            injectNormalDependency(bean, field);
        }

    }

    //xxx:这个是依照接口找实现类,还得处理条件注解,别提多辛苦了
    private void injectInterfaceTypeDependency(Object bean, Field field, MyBeanDefinition mb) throws IllegalAccessException, NoSuchFieldException {
        String packageName = (String) get("packageUrl");
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        Set<Class<?>> subTypesOf = new HashSet<>(reflections.getSubTypesOf(field.getType()));
        Set<Class<?>> classesToRemove = new HashSet<>();
        for (Class<?> implClass : subTypesOf) {
            MyConditional myConditionalAnnotation = implClass.getAnnotation(MyConditional.class);
            if (myConditionalAnnotation != null) {
                Class<? extends MyCondition> conditionClass = myConditionalAnnotation.value();
                MyCondition condition = (MyCondition) getBean(conditionClass.getName());
                autowireBeanProperties(condition, mb);
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
            log.error("多个实现类均命中,请添加合理的条件注解来进行选择性注入,冲突的实现类如下:\n{}", subTypesOf);
            throw new BeanCreationException("多个实现类命中");
        }
        if (selectedImpl != null) {
            Object dependency = getBean(selectedImpl.getName());
            field.setAccessible(true);
            field.set(bean, dependency);
        } else {
            throw new BeanCreationException("无法解析的依赖：" + field.getType());
        }
    }


    //xxx:注入一般Bean,依照字段查找类,从容器取出为字段赋值
    private void injectNormalDependency(Object bean, Field field) throws IllegalAccessException {
        Class<?> dependencyType = field.getType();
        //xxx:去容器里看看有没有啊,如果循环依赖了?没关系,反正容器里都是单例,注入一个未成熟的bean进去也无所谓,反正内存地址都一样
        Object dependency = getBean(dependencyType.getName());
        if (dependency == null) {
            log.warn("无法解析的依赖：{}", dependencyType.getName());
            return;
        }
        field.setAccessible(true);
        field.set(bean, dependency);

    }

    //xxx:注入配置文件
    private void injectValueAnnotation(Object instance, Field field) {
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
            log.error("依赖注入失败：{}", e.getMessage());
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

    public Map<String, Object> getIocContainer() {
        return singletonObjects;
    }

}
