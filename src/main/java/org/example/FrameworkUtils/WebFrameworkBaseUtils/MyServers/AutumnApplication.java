package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AutumnFactoriesLoader;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.BeanDefinitionLoader;
import org.example.FrameworkUtils.AutumnCore.Bootstrap.BootstrapRegistryInitializer;
import org.example.FrameworkUtils.AutumnCore.Bootstrap.DefaultBootstrapContext;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationListener;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.AutumnApplicationRunListener;
import org.example.FrameworkUtils.AutumnCore.Ioc.AnnotationConfigApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.AnnotationConfigServletWebServerApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationShutdownHook;
import org.example.FrameworkUtils.AutumnCore.Ioc.ConfigurableApplicationContext;
import org.example.FrameworkUtils.AutumnCore.context.ApplicationContextInitializer;
import org.example.FrameworkUtils.AutumnCore.env.ApplicationArguments;
import org.example.FrameworkUtils.AutumnCore.env.ConfigurableEnvironment;
import org.example.FrameworkUtils.AutumnCore.env.DefaultApplicationArguments;
import org.example.FrameworkUtils.AutumnCore.env.DefaultEnvironment;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author ziyuan
 * @since 2023.10
 */

/*
 * 核心启动类
 */
@Slf4j
public class AutumnApplication {

    private final Set<Class<?>> primarySources;

    private final List<BootstrapRegistryInitializer> bootstrapRegistryInitializers;

    private final Class<?> mainApplicationClass;

    private List<ApplicationContextInitializer> initializers;

    private List<ApplicationListener> listeners;

    private Map<String, List<String>> spiMap = AutumnFactoriesLoader.getSimpleBeans();

    private String[] sysArgs;

    private ApplicationContext beanFactory;

    private ConfigurableEnvironment environment;

    private static final ApplicationShutdownHook SHUTDOWN_HOOK = new ApplicationShutdownHook();


    //在构造方法中确定了扫描的起点以及推断真正的程序入口,并对SPI机制进行初始化,从Meta-INF目录下读取配置文件,进行实例化
    public AutumnApplication(Class<?>... primarySources) {
        /**
         * 非常感谢你能看到这里,能看到这段注释也说明你克隆了这段代码并且真的点进去看了,AutumnFramework仅仅是对Springboot的基础结构和基础功能进行简单的仿写
         * 对于springboot这样的庞然大物想要去梳理结构甚至看到细枝末节都是一件极其不易的事情
         * 作为一个称霸整个javaWeb生态的框架肩负了太多历史包袱,控制反转和面对接口编程在spring的源码中体现的淋漓尽致
         * 对于我们这样的学习者来说无疑更是增加了不少困难,在阅读,整理,理解的过程中我也买了不少的书,翻遍了各种博客也问遍了各种ai
         * 我能力有限,工作经验也并不丰富,这个项目我从大三下实习开始写起,到今天也有一年半的时间了,一转眼毕业上班也有半年了
         * 在编写项目的过程中还是止不住惊叹于spring生态的强大和复杂,在debug地狱中一层层的翻找,在无数个实现类和接口间闪转腾挪真的非常痛苦
         * 特别在设计AnnotationConfigApplicationContext,AutowiredAnnotationBeanPostProcessor,AnnotationAwareAspectJAutoProxyCreator,ConfigurationClassPostProcessor的时候简直抓耳挠腮,跟着博客打断点一点点观察运行情况
         * 这个bean从哪来的?这怎么就加载了?我的bean定义是被谁加载的?为什么BeanDefinitionRegistryPostProcessor还可以进行getBean操作?这个bean是怎么被代理的?怎么就被代理了
         * 经常几晚上的不出结果,或者出了结果又是运行异常.不过我依然觉得这是一件非常有意思的事,充满了挑战性
         *
         * 但一切都不是完美无缺的,我也经常在思考,是不是java这门历史包袱太重的语言到了今天已经充满了缺陷,从而需要springboot这样的框架来增强语言的能力
         * 不管事实是这样也好,还是仅仅是我的暴论也好,我都会通过注释写下我的真实写法,如果你也有一样的想法/不一致的想法 也欢迎分享出来
         */


        //确定应用的主要配置来源,为Bean扫描的起点
        this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));


        /**
         读取默认的BootstrapRegistryInitializer实现类,可以预注册组件,调用构造器创建实例对象
         在 ApplicationContext 准备就绪 但尚未refresh之前,提供一个回调入口 对这个 ApplicationContext 实例本身进行编程化的修改和配置
         一般而言可以用来动态加载一些配置
         */
        this.bootstrapRegistryInitializers = this.getAutumnFactoriesInstances(BootstrapRegistryInitializer.class);

        //读取默认的ApplicationContextInitializer实现类,可以修改 ApplicationContext,在Context创建后启用
        this.setInitializers(this.getAutumnFactoriesInstances(ApplicationContextInitializer.class));

        //注册监听器
        this.setListeners(this.getAutumnFactoriesInstances(ApplicationListener.class));

        //依照调用栈回溯到main方法,确定应用入口
        this.mainApplicationClass = deduceMainApplicationClass();

        //检查是否开启了编译参数,以便获取方法的真正参数名
        this.checkEnv();
    }

    public void run(String[] args) {

        //初始化命令行参数
        this.sysArgs = args;

        //创建DefaultBootstrapContext,在context没有创建之前提供一个容器,创建引导上下文
        DefaultBootstrapContext bootstrapContext = this.createBootstrapContext();

        //发布start事件,AutumnApplicationRunListener是一个内部监听器,不面向普通开发者
        List<AutumnApplicationRunListener> listeners = getAutumnFactoriesInstances(AutumnApplicationRunListener.class);
        for (AutumnApplicationRunListener listener : listeners) {
            listener.starting(bootstrapContext, mainApplicationClass);
        }

        //包裹命令行/jvm/其他参数
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);

        //初始化环境配置,environment于此被创建,并把主包,主类,主源进行写入保存,准备环境
        environment = prepareEnvironment(listeners, bootstrapContext, applicationArguments);

        //依照配置创建beanFactory,默认为`AnnotationConfigServletWebServerApplicationContext`
        beanFactory = createApplicationContext();

        // 准备上下文
        prepareContext(bootstrapContext, beanFactory, environment, listeners, applicationArguments);

        // 刷新上下文
        refreshContext((AnnotationConfigApplicationContext) beanFactory);

    }


    private void prepareContext(DefaultBootstrapContext bootstrapContext, ApplicationContext context, ConfigurableEnvironment environment, List<AutumnApplicationRunListener> listeners, ApplicationArguments applicationArguments) {
        context.setEnvironment(environment);
        bootstrapContext.setEnvironment(environment);

        //ApplicationContextInitializer中的initialize方法才开始被调用
        for (ApplicationContextInitializer initializer : initializers) {
            initializer.initialize(context);
        }
        for (AutumnApplicationRunListener listener : listeners) {
            listener.contextPrepared(context);
        }

        //打印banner
        log.info("""
                
                \033[35m                                _                         __  ____      _______\s
                \033[36m                     /\\        | |                       |  \\/  \\ \\    / / ____|
                \033[32m                    /  \\  _   _| |_ _   _ _ __ ___  _ __ | \\  / |\\ \\  / / |    \s
                \033[34m                   / /\\ \\| | | | __| | | | '_ ` _ \\| '_ \\| |\\/| | \\ \\/ /| |    \s
                \033[33m                  / ____ \\ |_| | |_| |_| | | | | | | | | | |  | |  \\  / | |____\s
                \033[31m                 /_/    \\_\\__,_|\\__|\\__,_|_| |_| |_|_| |_|_|  |_|   \\/   \\_____|\033[0m"""
        );
        //注册一些默认的bean,例如applicationArguments,environment等
        beanFactory.registerSingleton("applicationArguments", applicationArguments);
        beanFactory.registerSingleton("environment", environment);
        Set<Object> sources = getAllSources();

        /**
         *  load阶段,AnnotatedBeanDefinitionReader在此处被创建,委托创建BeanDefinitionHolder,进一步解析为BeanDefinition
         *  同时把主配置类进行注册
         *  同时ConfigurationClassPostProcessor于构造器中被创建,记住这个关键的类
         */
        load(context, sources.toArray(new Object[0]));
        for (AutumnApplicationRunListener listener : listeners) {
            listener.contextLoaded(context);
        }


    }

    //开始创建容器,项目中依赖配置容器判断生成类型
    private ConfigurableApplicationContext createApplicationContext() {

        if (ApplicationContext.BASE_CONTEXT.equals(environment.getProperty("autumn.beanFactory"))) {
            return new AnnotationConfigServletWebServerApplicationContext();
        } else {
            try {
                //AutumnCore.Ioc.MyContext 已彻底弃用
                Method getInstanceMethod = Class.forName("org.example.FrameworkUtils.AutumnCore.Ioc.MyContext").getDeclaredMethod("getInstance");
                getInstanceMethod.setAccessible(true);
                return (ConfigurableApplicationContext) getInstanceMethod.invoke(null);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private ConfigurableEnvironment prepareEnvironment(List<AutumnApplicationRunListener> listeners, DefaultBootstrapContext bootstrapContext, ApplicationArguments applicationArguments) {
        //environment于此被创建
        DefaultEnvironment environment = new DefaultEnvironment(applicationArguments);
        //TODO :应取消配置文件的硬编码
        //委托environment去自行解析,不应该整成硬编码,日后修改
        environment.loadConfiguration("src/main/resources/test.properties", ConfigurableEnvironment.DEFAULT_PROFILE);
        //拿到Properties对象,自己添加一些属性
        environment.getAllProperties().put("autumn.main.sources", mainApplicationClass.getName());
        environment.getAllProperties().put("autumn.main.package", mainApplicationClass.getPackageName());
        //发布environmentPrepared事件
        for (AutumnApplicationRunListener listener : listeners) {
            listener.environmentPrepared(environment);
        }
        bootstrapContext.setEnvironment(environment);
        return environment;
    }

    private DefaultBootstrapContext createBootstrapContext() {
        DefaultBootstrapContext bootstrapContext = new DefaultBootstrapContext();
        this.bootstrapRegistryInitializers.forEach((initializer) -> initializer.initialize(bootstrapContext));
        return bootstrapContext;
    }

    private void refreshContext(AnnotationConfigApplicationContext context) {
        //注册关机钩子
        SHUTDOWN_HOOK.registerApplicationContext(context);
        //前期工作彻底准备完成,开始refresh
        beanFactory.refresh();
    }




    public void setListeners(List<ApplicationListener> listeners) {
        this.listeners = new ArrayList<>(listeners);
    }

    public void setInitializers(List<? extends ApplicationContextInitializer> initializers) {
        this.initializers = new ArrayList<>(initializers);
    }

    public void addApplicationContextInitializer(ApplicationContextInitializer initializer) {
        this.initializers.add(initializer);
    }

    public void addApplicationListener(ApplicationListener listener) {
        this.listeners.add(listener);
    }


    public void addInitializers(BootstrapRegistryInitializer initializer) {
        this.bootstrapRegistryInitializers.add(initializer);
    }

    private Class<?> deduceMainApplicationClass() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(this::findMainClass)
                .orElse(null);
    }

    private Optional<Class<?>> findMainClass(Stream<StackWalker.StackFrame> stack) {
        return stack.filter((frame) -> Objects.equals(frame.getMethodName(), "main"))
                .findFirst()
                .map(StackWalker.StackFrame::getDeclaringClass);
    }


    private <T> List<T> getAutumnFactoriesInstances(Class<T> type) {
        try {
            List<String> initializers = new ArrayList<>(spiMap.get(type.getSimpleName()));
            List<T> result = new ArrayList<>();
            for (String className : initializers) {

                Class<?> clazz = Class.forName(className);
                if (type.isAssignableFrom(clazz)) {
                    result.add((T) clazz.getDeclaredConstructor().newInstance());
                }

            }
            return result;
        } catch (Exception e) {
            log.warn("装载失败", e);
            return List.of();
        }

    }

    public Set<Object> getAllSources() {
        return new LinkedHashSet<>(this.primarySources);
    }

    protected void load(ApplicationContext context, Object[] sources) {
        BeanDefinitionLoader loader = new BeanDefinitionLoader(context, sources);
        loader.load();
    }


    private void checkEnv() {
        try {
            //如果不开额外的编译参数,Java会把方法参数名都去掉变成例如arg1/arg2这样的无意义简写,框架无法获取真实的方法参数名,就得使用参数注解来标记参数名字
            Parameter[] parameters = AutumnApplication.class.getDeclaredMethod("test", String.class, String.class).getParameters();
            String[] expectedParamNames = {"test", "test2"};
            for (int i = 0; i < parameters.length; i++) {
                String paramName = parameters[i].getName();
                if (!paramName.equals(expectedParamNames[i])) {
                    log.error("你应该打开编译参数: -parameters,现在框架无法获取真实的方法参数名");
//                    throw new RuntimeException("请打开编译参数: -parameters");
                }

            }
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void test(String test, String test2) {

    }






}
