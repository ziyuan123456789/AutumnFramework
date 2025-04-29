package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.Annotations.CheckParameter;
import org.example.Annotations.EnableAop;
import org.example.Bean.Car;
import org.example.Config.BeanTestConfig;
import org.example.Config.Test;
import org.example.FrameworkUtils.AutumnCore.Annotation.Lazy;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;
import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.ContextClosedEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationListener;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactory;
import org.example.FrameworkUtils.Orm.MyRedis.MyReidsTemplate;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Cookie.Cookie;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.WebSocket.MyWebSocket;
import org.example.Injector.ColorMappingEnum;
import org.example.mapper.UpdateMapper;
import org.example.mapper.UserMapper;
import org.example.service.AsyncService;
import org.example.service.CacheTestService;
import org.example.service.LoginService;
import org.example.service.TransactionService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.04
 */
@MyController
@Slf4j
public class AutumnTestController implements BeanFactoryAware, ApplicationListener<ContextClosedEvent> {

    private ApplicationContext beanFactory;

    @MyAutoWired
    private AsyncService asyncService;

    //测试配置文件注入器
    @Value("url")
    private String sqlUrl;

    //测试自身循环依赖
    @MyAutoWired
    private AutumnTestController autumnTestController;

    @MyAutoWired
    private LoginService loginService;

    @MyAutoWired
    private UserMapper userMapper;

    @MyAutoWired
    @Lazy
    private MyReidsTemplate myReidsTemplate;

    @MyAutoWired("postProcessChange")
    private Car car;

    @MyAutoWired
    private SqlSessionFactory sqlSessionFactory;

    @MyAutoWired
    private Test test;

    @MyAutoWired
    private AutumnRequest autumnRequest;

    @MyAutoWired
    private AutumnResponse autumnResponse;


    @MyAutoWired
    private CacheTestService cacheTestService;

    @MyAutoWired
    private UpdateMapper updateMapper;

    @MyAutoWired
    private TransactionService transactionService;

    @MyAutoWired
    private BeanTestConfig beanTestConfig;

    @MyRequestMapping("/cglib")
    public String getBean() {
        log.debug(beanTestConfig.getClass().getName());
        return beanTestConfig.giveMeWenJieCar().toString();
    }

    //测试事务
    @MyRequestMapping("/transaction")
    public String transactionTest() throws SQLException {
        return transactionService.transactionTest();
    }


    //测试minebatis增删改查
    @MyRequestMapping("/crud")
    public Object crudKing(String method) {
        return switch (method) {
            case "insert" -> updateMapper.insertUser("test", "0", "test", "收到");
            case "update" -> updateMapper.updateUserById("test1", "0", "test3", 1);
            case "delete" -> updateMapper.deleteUserById(1);
            default -> Integer.MAX_VALUE;
        };
    }

    //测试缓存组件
    @MyRequestMapping("/cache")
    public String cacheTest(String name) {
        return cacheTestService.cacheTest(name);

    }


    //测试自定义注入规则
    @MyRequestMapping("/inject")
    public String injectTest(ColorMappingEnum color) {
        return color.getColorName();
    }

    //测试异步能力
    @MyRequestMapping("/async")
    public String asyncTest() {
        asyncService.asyncTest();
        return "异步测试";
    }

    //测试全局request功能
    @MyRequestMapping("/request")
    public String requestTestWithField() {
        log.info(myReidsTemplate.getClass().toString());
        log.info("{}{}{}", autumnRequest.getUrl(), autumnRequest.getMethod(), autumnRequest.getParameters());
        return autumnRequest.getUrl() + autumnRequest.getMethod() + autumnRequest.getParameters();
    }

    //测试方法级request功能
    @MyRequestMapping("/requestmethod")
    public String requestTestWithMethodParma(AutumnRequest res) {
        log.info("{}{}{}", res.getUrl(), res.getMethod(), res.getParameters());
        return res.getUrl() + res.getMethod() + res.getParameters();
    }


    //测试response与setCookie功能
    @MyRequestMapping("/response")
    public void responseTest(AutumnResponse myResponse) {
        Cookie cookie = new Cookie("newcookie", "session1");
        myResponse.setCode(200)
                .setCookie(cookie)
                .setView(new View("AutumnFrameworkMainPage.html"))
                .outputHtml();
    }

    //测试参数注入
    @MyRequestMapping("/paramTest")
    public String paramTest(String name, String age) {
        return name + "+" + age;
    }

    //循环依赖测试
    @MyRequestMapping("/cycletest")
    public Map<String, Object> cycleTest() {
        return autumnTestController.mapTest();
    }

    //测试@Bean("BeanName")功能是否正常,同时看看Json解析器好不好用
    @MyRequestMapping("/map")
    public Map<String, Object> mapTest() {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put("url", sqlUrl);
        log.info(car.toString());
        log.info(test.toString());
        return myMap;
    }

    //测试redis
    @MyRequestMapping("/redis")
    public String redis() {
        myReidsTemplate.init();
        myReidsTemplate.set("test", "test");
        return myReidsTemplate.toString() + "\n" + myReidsTemplate.get("test");
    }

    //测试View层功能
    @MyRequestMapping("/html")
    public View myhtml() {
        return new View("AutumnFrameworkMainPage.html");
    }


    //测试session功能
    @MyRequestMapping("/session")
    public String session(AutumnRequest myRequest) {
        String sessionId = myRequest.getSession().getSessionId();
        myRequest.getSession().setAttribute("name", sessionId);
        return "切换阅览器查看唯一标识符是否变化? 标识符如下:" + myRequest.getSession().getAttribute("name");
    }

    //测试WebSocket功能
    @MyRequestMapping("/websocket")
    public MyWebSocket websocketTest() {
        return new MyWebSocket();
    }

    //测试数据库功能
    @EnableAop
    @MyRequestMapping("/Login")
    public String login(@CheckParameter String username,
                        String password) {
        if (loginService.checkLogin(username, password)) {
            return "登录成功";

        } else {
            return "登录失败";
        }

    }

    //测试数据库功能
    @MyRequestMapping("/getall")
    public String getAll() {
        return userMapper.getAllUser(0).toString();
    }


    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info(("接受到容器关闭信号"));

    }

    @Override
    public boolean supportsEvent(ApplicationEvent event) {
        return event instanceof ContextClosedEvent;
    }
}
