package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.Annotations.CheckParameter;
import org.example.Aop.UserAopProxyFactory;
import org.example.Bean.Temp;
import org.example.FrameworkUtils.Annotation.EnableAop;
import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyController;
import org.example.FrameworkUtils.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.Annotation.MyRequestParam;
import org.example.FrameworkUtils.Annotation.Value;
import org.example.FrameworkUtils.Cookie.Cookie;
import org.example.FrameworkUtils.Orm.MyRedis.MyReidsTemplate;
import org.example.FrameworkUtils.ResponseType.Response;
import org.example.FrameworkUtils.ResponseType.Views.View;
import org.example.FrameworkUtils.Webutils.Request;
import org.example.service.LoginService;
import org.example.service.TestService;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2023.10
 */
@MyController
@Slf4j
@EnableAop(getMethod = {"myhtml","login"}, getClassFactory = UserAopProxyFactory.class)
public class AdminController {

    @MyAutoWired
    LoginService loginService;
    @MyAutoWired
    TestService testService;
    @Value("url")
    String sqlUrl;

    @MyAutoWired
    Temp t;

    @MyAutoWired
    MyReidsTemplate myReidsTemplate;
    Integer id=1;


    @MyRequestMapping("/map")
    public Map<String, Integer> maptest(Request request)  {
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
    public void beantest(Request request,Response response) {
        Cookie cookie=new Cookie("newcookie","session1");
        response.setCode(200)
                .setCookie(cookie)
                .setView(new View("AutumnFrameworkMainPage.html"))
                .outputHtml();
    }

    @MyRequestMapping("/session")
    public String session(Request request) {
        String sessionId=request.getSession().getSessionId();
        request.getSession().setAttribute("name",sessionId);
        return (String) request.getSession().getAttribute("name");
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
    public String login(@MyRequestParam("username") @CheckParameter String username,
                        @MyRequestParam("password") String password,Request request) {
        if(loginService.login(username,password)){
            return request.getMethod()+request.getUrl()+username+"\n登陆成功";

        }else{
            return "登录失败";
        }

    }


    @MyRequestMapping("/admin")
    public void adminMainPage(Request request, Response response) throws IOException {
        testService.cycle();

    }


    @MyRequestMapping("/testt")
    public String sayHello(){
        return sqlUrl;
    }



}
