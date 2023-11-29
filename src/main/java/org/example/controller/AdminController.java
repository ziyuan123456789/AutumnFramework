package org.example.controller;
import javassist.expr.NewArray;
import lombok.extern.slf4j.Slf4j;
import org.example.Annotations.CheckParameter;
import org.example.Aop.UserAopProxyFactory;
import org.example.Bean.Temp;
import org.example.Bean.User;
import org.example.FrameworkUtils.Annotation.EnableAop;
import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyController;
import org.example.FrameworkUtils.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.Annotation.MyRequestParam;
import org.example.FrameworkUtils.Annotation.Value;
import org.example.FrameworkUtils.Orm.MyRedis.MyReidsTemplate;
import org.example.FrameworkUtils.ResponseType.Views.View;
import org.example.FrameworkUtils.Webutils.Json.JsonFormatter;
import org.example.FrameworkUtils.Webutils.MyContext;
import org.example.FrameworkUtils.Webutils.Request;
import org.example.service.LoginService;
import org.example.service.TestService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangzhiyi
 * @since 2023.10
 */
@MyController
@Slf4j
@EnableAop(getMethod = {"myhtml","login"}, getClassFactory = UserAopProxyFactory.class)
public class AdminController {

    private MyContext myContext = MyContext.getInstance();
    @Value("url")
    String sqlUrl;
    @Value("user")
    String user;
    @Value("password")
    String password;

    @MyAutoWired
    LoginService loginService;
    @MyAutoWired
    TestService testService;

    @MyAutoWired
    Temp t;

    @MyAutoWired
    MyReidsTemplate myReidsTemplate;

    @MyRequestMapping("/map")
    public Map<String, Integer> maptest(Request request) throws IllegalAccessException {
        Map<String,Integer>myMap =new HashMap<>();
        myMap.put("user",123);
        myMap.put("password",456);
        return myMap;
    }

    @MyRequestMapping("/redis")
    public String redis(Request request) {
        myReidsTemplate.init();
        myReidsTemplate.set("test","test");
        return myReidsTemplate.toString()+myReidsTemplate.get("test");
    }

    @MyRequestMapping("/myhtml")
    public View myhtml(Request request) {
        return new View("AutumnFrameworkMainPage.html");
    }

    @MyRequestMapping("/bean")
    public String beantest(Request request) {
        return t.toString();
    }

    @MyRequestMapping("/uploadpage")
    public View fileupload(Request request) {
        return new View("uploadfile.html");
    }

    @MyRequestMapping("/upload")
    public String upload(@MyRequestParam("str") String str) {
        return "123";
    }

    @MyRequestMapping("/login")
    public String login(@CheckParameter Request request) {

        Map<String,String> requestMap = request.getParameters();
        String username= requestMap.get("username");
        String password= requestMap.get("password");
        if(loginService.login(username,password)){
            return request.getMethod()+request.getUrl()+request.getParameters()+"\n登陆成功";

        }else{
            return "登陆失败";
        }

    }


    @MyRequestMapping("/admin")
    public String adminMainPage(Request request) throws InterruptedException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(sqlUrl)
                .append("\t\t\t\t")
                .append(user)
                .append("\t\t\t")
                .append(password);
        return Thread.currentThread().getName();
    }


    @MyRequestMapping("/testt")
    public String sayHello(){
        return sqlUrl;
    }



}
