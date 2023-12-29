# AutumnFramework
对spring的拙劣的模仿

![Java17](https://img.shields.io/badge/JDK-17+-success.svg)
[![License](https://img.shields.io/npm/l/echarts?color=5470c6)]()

## 注意事项:
- ~~cglib不支持java17以及之后的版本了,降低jdk版本~~
- 编译结束后方法形参名称不再保留,虽然可以加入编译参数解决,但是为了泛用性选择了形参注解标注形参,mapper接口层同理
- 目前仅支持调用字段的无参默认构造器注入,以后可能会修改

## 打个广告:
大四没事干,找了个java实习一个月2000,大连的hr有没有想联系我的 邮箱:3139196862@qq.com

## 项目描述:
不依赖TomCat,Servlet等技术实现的网络服务框架,参照了Mybatis,SpringMvc等设计思想从0手写了一个基于注解的仿SpringBoot框架

## 已经实现:
- socket实现的网络服务,简单的支持网页输出,GET,POST方法传参,controller声明形参直接注入
- url-method映射,实现url与controller方法的路由表
- 由三级缓存组成的ioc容器,解决循环依赖的问题
- 非常弱的依赖注入实现,可以实现@autowired标注的字段的自动注入,以及@value注入配置文件,还可以注入接口的实现类,基本上和springboot写法一样了
- cglib实现的aop,稍微解决了代理类注解擦除的问题,可以正常依赖注入
- mybaits的简易实现,使用jdk动态代理接口,实现参数映射以及实体类映射
- 加入了责任链模式的过滤器,~~使用@order声明顺序~~
- 实现@bean功能,添加配置类,配置框架行为
- 用户重写autumnMvcConfig接口覆盖默认实现类,实现自定义首页面,icon等
- 简易的redistemplate加入,可以连接redis了
- 加入了Json与JavaBean的转换,可以跟前端用Json通讯了
- 类级别的条件注解的完整加入,用户可以自定义处理器了,只需要实现condition接口覆盖match逻辑
- response类加入,用户可以选择自己来控制返回头和内容,例如进行setCookie操作
- cookie,session加入,自动为新用户setCookie,设置JSESSIONID
- 依照JSESSIONID的value查找对应的session
- 简易的swagger加入
- 循环依赖提示器加入

## 代码示范
### Controller
```java
@MyAutoWired
LoginService loginService;
@MyAutoWired
TestService testService;
@Value("url")
String sqlUrl;

@MyRequestMapping("/login")
public String login(@MyRequestParam("username") @CheckParameter String username,
                    @MyRequestParam("password") String password,Request myRequest) {
    if(loginService.login(username,password)){
        return myRequest.getMethod()+myRequest.getUrl()+username+"\n登录成功";
    }else{
        return "登录失败";
    }
}
@MyRequestMapping("/myhtml")
public View myhtml(Request myRequest) {
    return new View("AutumnFrameworkMainPage.html");
}

@MyRequestMapping("/responseTest")
public void responseTest(Request myRequest,Response myResponse) {
    Cookie cookie=new Cookie("newcookie","session1");
    myResponse.setCode(200)
            .setCookie(cookie)
            .setView(new View("AutumnFrameworkMainPage.html"))
            .outputHtml();
}

@MyRequestMapping("/session")
public String session(Request myRequest) {
    String sessionId=myRequest.getSession().getSessionId();
    myRequest.getSession().setAttribute("name",sessionId);
    return (String) myRequest.getSession().getAttribute("name");
}
```
### Service
```java
public interface LoginService {
    boolean login(String username, String password);
}
@MyService
public class LoginServiceImpl implements LoginService {
    @MyAutoWired
    UserMapper userMapper;
    @Override
    public boolean login(String username, String password) {
        return userMapper.login(username, password) != null;
    }
}
```
### AOP
```java
@MyController
@Slf4j
@EnableAop(getMethod = {"myhtml","login"}, getClassFactory = UserAopProxyFactory.class)
public class AdminController {

    @MyAutoWired
    LoginService loginService;
}
```
```java
@Slf4j
@MyAspect
public class UserAopProxyHandler implements AutunmnAopFactory {
    @Override
    public void doBefore(Object obj, Method method, Object[] args) {
        log.warn("用户切面方法开始预处理,切面处理器是"+this.getClass().getName()+"处理的方法为:"+method.getName() );
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation.annotationType().equals(CheckParameter.class)) {
                    log.error("参数"+args[i].getClass().getSimpleName()+"被拦截");
                    args[i] = "AopCheck";
                }
            }
        }
        return proxy.invokeSuper(obj, args);
    }

    @Override
    public void doAfter(Object obj, Method method, Object[] args) {
        log.info("用户自定义逻辑执行结束");
    }

    @Override
    public void doThrowing(Object obj, Method method, Object[] args,Exception e) {
        log.error("用户切面方法抛出异常",e);
    }
}

```
### 拦截器
```java
@Slf4j
@MyComponent
@MyOrder(1)
public class UrlFilter implements Filter {
    @MyAutoWired
    IndexFilter indexFilter;

    @Override
    public boolean doChain(MyRequest myRequest, MyResponse myResponse) {
        if ("GET".equals(myRequest.getMethod())) {
            log.info("一级过滤链拦截,开始第一步鉴权");
//            myResponse.setCode(400).setResponseText("鉴权失败").outputErrorMessage();
            return indexFilter.doChain(myRequest, myResponse);
        } else {
            log.info("一级过滤链放行");
            return false;
        }
    }
}

```
### Mapper
```java
@MyMapper
public interface UserMapper {
    @MySelect("select username,password from user where username=#{username} and password=#{password}")
    User login(@MyParam("username") String username,@MyParam("password") String password);
    @MyInsert("insert into  user (username,password) values (#{username},#{password})")
    Integer insertUser(@MyParam("username") String username,@MyParam("password") String password);
}

```
### 配置类,@Bean
```java
@MyConfig
public class beanConfig {
    @MyAutoWired
    UserMapper userMapper;

    @MyAutoWired
    TestMapper testMapper;
    @AutunmnBean
    public User beanTest(){
        return  userMapper.login("wzy","123");
    }

    @AutunmnBean
    public Temp beanTest1() {
        return testMapper.selectById1(1);
    }
}
```
```java
@MyConfig
public class CrossOriginConfig implements AutumnMvcCrossOriginConfig {

    CrossOriginBean crossOrigin=new CrossOriginBean();

    @Override
    @AutunmnBean
    public CrossOriginBean setAllowCrossOrigin() {
        crossOrigin.setOrigins("*");
        return crossOrigin;
    }
}

```
### 条件注解
```java
@MyService
@MyConditional(MatchClassByInterface.class)
public class AutumnMvcConfigurationBaseImpl implements AutumnMvcConfiguration{
    @Value("baseHtml")
    String baseHtml;

    @Value("404Html")
    String notFoundPage;
    @Override
    public View getMainPage() {
        return new View(baseHtml);
    }

    @Override
    public View get404Page() {
        return new View(notFoundPage);
    }
}
```
```java
@MyComponent
@Slf4j
public class MatchClassByInterface implements MyCondition {

    @MyAutoWired
    private Reflections reflections;

    @Override
    public void init(){
        log.info(this.getClass().getSimpleName() + "条件处理器中的初始化方法被执行");
    }


    @Override
    public boolean matches(MyContext myContext, Class<?> clazz) {
        Set<Class<?>> subTypesOf = (Set) reflections.getSubTypesOf(clazz.getInterfaces()[0]);
        List<Class> injectImplList=new ArrayList<>();
        if (subTypesOf.size() == 2) {
            return false;
        } else if (subTypesOf.size() > 2) {
            for (Class<?> implClass : subTypesOf) {
                if(implClass.equals(clazz)){
                    continue;
                }
                MyConditional myCondition = implClass.getAnnotation(MyConditional.class);
                if (myCondition != null) {
                    Class<? extends MyCondition> otherCondition = myCondition.value();
                    MyCondition myConditionImpl = myContext.getBean(otherCondition);
                    myConditionImpl.init();
                    if (myConditionImpl.matches(myContext, implClass)) {
                        throw new IllegalStateException("多个条件处理器均被命中,请确认到底要注入哪一个"+injectImplList);
                    }
                    myConditionImpl.after();
                }else{
                    injectImplList.add(implClass);
                }
            }
        }
        if(injectImplList.size()==1){
            return false;

        }else{
            throw new IllegalStateException("多个条件处理器均被命中,请确认到底要注入哪一个");
        }

    }
}

```
### 配置文件
```html
url=jdbc:mysql://localhost:3306/threeproject?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true
user = root
password=root
port=8080
cookieKeepTime=180000
threadPoolNums=10
htmlHome=HTML
iconHome=Icon
baseHtml=AutumnFrameworkMainPage.html
404Html=404.html
iconName=myicon.ico
crossOrigin=*
redisHost=127.0.0.1
redisPort=6379
allow-circular-references=true
```
## 未来打算实现:

- 解决代码耦合严重的问题,再下去就要臭不可闻了(修改了大量ioc容器代码,解除大量耦合和莫名其妙的代码,逻辑更加清晰了)
- ~~手写的mybaits增加 增删改的功能(已实现)~~
- 实现文件上传的功能 (半实现)
- 加入类似于spring的事务,支持回滚
- 实现自己写的http服务器与servlet或者其他成熟的web框架的切换(@bean+条件注解实现)
- ~~修正icon获取不到的问题 (已经解决)~~
- ~~实现json输出的功能(已经实现)~~
- 加入对Cookie的支持(已实现,等待测试)
- 加入对Session的支持(已实现,等待测试)
- 加入对Token的支持
- controller方法形参直接注入JavaBean
- 增加JVM关闭钩子,在关机的时候把session序列化到redis/mysql等保存,下次启动先恢复
- request和response承担了过多的责任,考虑分出更多的类

## 更长远的想法:
- 加入beandefinition
- 加入真正可用的aop(aop定义权限现在已经移交给用户,可以指定处理器和拦截方法了)
- 加入websocket
- 架构设计的稀碎,核心组件每一个都违背单一原则,有时间进行推倒重来,大范围重构
- 使用c#重构这个框架
- 使用python重构这个框架

## 人生目标:
- 找到月薪3000的实习
- ~~找个美女对象(即将实现)(已经黄了)~~

## 人生忠告:
- 技术没什么意义,多发展一下自己在生活中的兴趣爱好,人格的均衡发展才是硬道理
- 远离infp女生
- 远离情绪黑洞

## 尚未解决的难点:
- aop指定被代理方法时候框架没办法判断重载,以后会进行完善
- ~~@bean与依赖注入时机的问题:举个例子配置类a定义一个标注有@bean的方法,我的框架反射执行这个方法拿到object放入第一缓存.接着业务类b依赖这个bean,成功注入.但是有时候是业务类b先走到依赖注入这个环节,这时候因为@bean标注的是一个方法而不是一个类因此第一二三级缓存中没有这个元素,初始化失败.不知道spring是如何解决这个问题的,我目前的解决方法是初始化容器两次,勉勉强强解决了把(11/29已解决)~~

## 更新记录:
### 2023/12/29
- 增加了循环依赖检测器,可以像spring boot那样检测循环依赖并输出哪些Bean有循环引用的问题
- 可以在配置文件中添加allow-circular-references=true开启循环依赖
- 增加了更多配置文件注入器的类型转换功能

### 2023/12/26
- 修正了注入接口实现类的时候有多个实现类但框架不报错的问题
- 现在可以通知用户是哪些实现类冲突,而不是一个个去查
- 在maven中添加了编译参数可以直接拿到参数真实名字了,形参注解将会取消

### 2023/12/25
- 优化了过滤器,可以手动接管response对象控制输出流
- 加入了一个简易的swagger,反正能看到url-method映射表,以及需要什么参数返回什么类型,以后加入网络请求的功能

### 2023/12/22
- 优化了对controller方法返回值判断的能力.可以正确区分是有返回值但是返回null还是没有返回值使用response接管输出流,不过无论你是否用了response接管,框架都会正确输出内容


### 2023/12/21
- 使用了一些泛型,避免每次的强制转换
- 手搓的的mybatis可以进行完整的curd了
- 对mapper层的返回值放宽了要求,可以进行简易的转换

### 2023/12/12
- 实现了session,根据cookie中的sessionid来向sessionmanger请求获得mysession实例
- 实现了cookie,可以手动接管response进行setCookie,在解析myrequest对象的时候也会自动填充cookie数组同时看看有没有标记session的cookie
### 2023/12/6
- 现在aop工厂传递给用户切面处理器的是父类的method,避免了用户自己实现繁琐地查找被擦除注解
### 2023/12/1
- 修正了条件注解的bug,还有@bean实现的bug.依赖注入被完整实现
- response类被加入,用户可以选择自己获取输出的权柄,来定制返回头和内容,只需要在方法参数内加入response对象,框架便可以自动注入,方便用户最大话控制输出,并实现setCookie等操作

### 2023/11/30
- 解除ioc,di部分大量耦合,代码更简单易懂了,删除了部分莫名其妙的逻辑
- 优化了日志,禁止了Reflections库的日志输出,同时添加了彩色日志
- 条件注解开放给用户,实现condition接口便可以指定处理器,重写match方法,实现自己的逻辑,当多个实现类存在并且条件注解都通过则抛出异常

### 2023/11/29
- 完善了@bean功能,同时可在这个配置类正常依赖其他内容,并且不会再受容器依赖注入顺序变化而崩溃了,取消了直接放入一级缓存的做法,变为在第三级缓存放入lambda表达式,需要的时候生产自己,并进行依赖注入,成熟之后反射执行这个方法
- 使用spring提供的cglib模块来进行动态代理,因此jdk版本提升到了19,也消除了jvm非法反射警告


### 2023/11/27
- ~~实现了@bean功能,可以在配置类配置方法注入bean,直接放入一级缓存,作为成熟的bean存在,在其他组件可以顺利注入~~

### 2023/11/23
- 现在Aop定义权开放给用户
- 拦截后处理切面逻辑也交给用户,同时隐藏了cglib的实现,只需要关心逻辑


### 2023/11/22
- 完成了配置类的注入,当用户实现autumnmvcconfig接口便可以更改框架的部分逻辑,例如默认登陆页面
- 条件注解的雏形加入
- 完善了POST方法,可以获取body了
- 修改了request对象,暴露了一些新的接口

### 2023/11
- 实现了注解扫描器,扫描controller实现url-method映射表
- 改进了Ioc容器,增加了注解标识的字段的依赖注入功能
- 改进了di逻辑,可以注入配置文件和字段了,仿的mybaits mapper层也能注入了
- 改进了di逻辑,可以为接口自动注入实现类
- 改进了Ioc容器,三级缓存被加入,解决循环依赖的问题
- 改进了Ioc的第三级缓存,可以注入Cglib创建的代理
- 解决代理子类注解丢失的情况,动态代理也可以依赖注入了
- 实现了一些框架默认操作,默认首页,默认Icon等
- 增加了视图层,可以返回HTML了
- 不再要求方法一定要加入request对象作为形参
- 实现了Controller形参注入器,直接在参数上写需要的字段,框架解析Url自动注入
- 实现了类似Mybatis的#{}拼接Sql,接口声明参数直接映射
- 增加了几个异常类,更直观定位问题
- 反射方法报错重启服务,程序不再直接死掉

### 2023/10
- 实现了套接字实现的简单web服务器,可以在web上输出一些字
- 实现了过滤器

### 2023/6
- 实现了对Jdbc的简易封装
- 实现对接口的代理
- 实现了代理工厂,生产创建好的代理类

### 2023/5
- 实现了简易的Ioc容器,使用容器实现单例模式
- 实现了对JavaBean的字段注入(@Value注解标记)

### 2022/?
- 了解到了Java反射







### 感谢:
没有Gpt4写代码寸步难行