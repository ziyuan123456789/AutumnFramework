package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.Annotations.CheckParameter;
import org.example.Bean.Car;
import org.example.Config.Test;
import org.example.FrameworkUtils.AutumnCore.Annotation.EnableAop;
import org.example.FrameworkUtils.AutumnCore.Annotation.Lazy;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestParam;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSession;
import org.example.FrameworkUtils.Orm.MyRedis.MyReidsTemplate;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Cookie.Cookie;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.WebSocket.MyWebSocket;
import org.example.Injector.ColorMappingEnum;
import org.example.mapper.UserMapper;
import org.example.service.AsyncService;
import org.example.service.CacheTestService;
import org.example.service.LoginService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.04
 */
@MyController
@Slf4j
public class AutumnTestController {
    //xxx:测试配置文件注入器
    @Value("url")
    private String sqlUrl;

    //xxx:测试自身循环依赖
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
    private SqlSession sqlSession;

    @MyAutoWired
    private Test test;

    @MyAutoWired
    private AutumnRequest autumnRequest;

    @MyAutoWired
    private AutumnResponse autumnResponse;

    @MyAutoWired
    private AsyncService asyncService;

    @MyAutoWired
    private CacheTestService cacheTestService;

    //xxx:测试缓存组件
    @MyRequestMapping("/cache")
    public String cacheTest(@MyRequestParam("name") String name) {
        return cacheTestService.cacheTest(name);
    }


    //xxx:测试自定义注入规则
    @MyRequestMapping("/inject")
    public String injectTest(ColorMappingEnum color) {
        return color.getColorName();
    }

    //xxx:测试异步能力
    @MyRequestMapping("/async")
    public String asyncTest() {
        asyncService.asyncTest();
        return "异步测试";
    }

    //xxx:测试全局request功能
    @MyRequestMapping("/request")
    public String requestTestWithField() {
        log.info(myReidsTemplate.getClass().toString());
        log.info("{}{}{}", autumnRequest.getUrl(), autumnRequest.getMethod(), autumnRequest.getParameters());
        return autumnRequest.getUrl() + autumnRequest.getMethod() + autumnRequest.getParameters();
    }

    //xxx:测试方法级request功能
    @MyRequestMapping("/requestmethod")
    public String requestTestWithMethodParma(AutumnRequest autumn) {
        log.info("{}{}{}", autumn.getUrl(), autumn.getMethod(), autumn.getParameters());
        return autumn.getUrl() + autumn.getMethod() + autumn.getParameters();
    }


    //xxx:测试response与setCookie功能
    @MyRequestMapping("/response")
    public void responseTest(AutumnResponse myResponse) {
        Cookie cookie = new Cookie("newcookie", "session1");
        myResponse.setCode(200)
                .setCookie(cookie)
                .setView(new View("AutumnFrameworkMainPage.html"))
                .outputHtml();
    }

    //xxx:测试参数注入
    @MyRequestMapping("/paramTest")
    public String paramTest(@MyRequestParam("name") String name, @MyRequestParam("age") String age) {
        return name + age;
    }

    //xxx:循环依赖测试
    @MyRequestMapping("/cycletest")
    public Map<String, Object> cycleTest() {
        return autumnTestController.mapTest();
    }

    //xxx:测试@Bean("BeanName")功能是否正常,同时看看Json解析器好不好用
    @MyRequestMapping("/map")
    public Map<String, Object> mapTest() {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put("url", sqlUrl);
        log.info(car.toString());
        log.info(test.toString());
        return myMap;
    }

    //xxx:测试redis
    @MyRequestMapping("/redis")
    public String redis() {
        myReidsTemplate.init();
        myReidsTemplate.set("test", "test");
        return myReidsTemplate.toString() + "\n" + myReidsTemplate.get("test");
    }

    //xxx:测试View层功能
    @MyRequestMapping("/html")
    public View myhtml() {
        return new View("AutumnFrameworkMainPage.html");
    }


    //xxx:测试session功能
    @MyRequestMapping("/session")
    public String session(AutumnRequest myRequest) {
        String sessionId = myRequest.getSession().getSessionId();
        myRequest.getSession().setAttribute("name", sessionId);
        return "切换阅览器查看唯一标识符是否变化? 标识符如下:"+myRequest.getSession().getAttribute("name");
    }

    //xxx:测试WebSocket功能
    @MyRequestMapping("/websocket")
    public MyWebSocket websocketTest() {
        return new MyWebSocket();
    }

    //xxx:测试数据库功能
    @EnableAop
    @MyRequestMapping("/Login")
    public String login(@MyRequestParam("username") @CheckParameter String userId,
                        @MyRequestParam("password") String password) {
        if (loginService.checkLogin(userId, password)) {
            return "登录成功";

        } else {
            return "登录失败";
        }

    }

    //xxx:测试数据库功能
    @MyRequestMapping("/getall")
    public String getAll() {
        return userMapper.getAllUser(0).toString();
    }


}
