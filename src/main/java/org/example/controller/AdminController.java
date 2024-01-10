package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.Annotations.CheckParameter;
import org.example.Aop.UserAopProxyHandler;
import org.example.Bean.Car;
import org.example.Bean.Temp;
import org.example.FrameworkUtils.AutumnMVC.Annotation.EnableAop;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyController;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyRequestParam;
import org.example.FrameworkUtils.AutumnMVC.Annotation.Value;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Cookie.Cookie;
import org.example.FrameworkUtils.Orm.MyRedis.MyReidsTemplate;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyResponse;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;
import org.example.mapper.CarMapper;
import org.example.service.LoginService;
import org.example.service.TestService;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2023.10
 */
@MyController
@Slf4j
@EnableAop(getMethod = {"myhtml","login"}, getClassFactory = UserAopProxyHandler.class)
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

    @MyAutoWired
    AdminController adminController;

    @MyAutoWired
    ArrayList<Car> carList;



    @MyRequestMapping("/map")
    public Map<String, Object> mapTest(MyRequest myRequest)  {
        Map<String,Object>myMap =new HashMap<>();
        myMap.put("user",123);
        myMap.put("password",456);
        return  myMap;

    }
    @MyRequestMapping("/insert")
    public Integer insert(@MyRequestParam("username") String username,@MyRequestParam("password") String password){
        return loginService.insertUser(username,password);
    }


    @MyRequestMapping("/redis")
    public String redis(MyRequest myRequest) {
        myReidsTemplate.init();
        myReidsTemplate.set("test","test");
        return myReidsTemplate.toString()+myReidsTemplate.get("test");
    }
    @MyRequestMapping("/testvoid")
    public void testvoid(MyResponse myResponse) {

    }



    @MyRequestMapping("/myhtml")
    public View myhtml(MyRequest myRequest) {
        return new View("AutumnFrameworkMainPage.html");
    }

    @MyRequestMapping("/responseTest")
    public void responseTest(MyRequest myRequest, MyResponse myResponse) {
        Cookie cookie=new Cookie("newcookie","session1");
        myResponse.setCode(200)
                .setCookie(cookie)
                .setView(new View("AutumnFrameworkMainPage.html"))
                .outputHtml();
    }

    @MyRequestMapping("/session")
    public String session(MyRequest myRequest) {
        String sessionId= myRequest.getSession().getSessionId();
        myRequest.getSession().setAttribute("name",sessionId);
        return (String) myRequest.getSession().getAttribute("name");
    }



    @MyRequestMapping("/uploadpage")
    public View fileupload(MyRequest myRequest) {
        return new View("uploadfile.html");
    }

    @MyRequestMapping("/upload")
    public String upload(@MyRequestParam("str") String str) {
        return "123";
    }

    @MyRequestMapping("/login")
    public String login(@MyRequestParam("username") @CheckParameter String username,
                        @MyRequestParam("password") String password, MyRequest myRequest) {
        if(loginService.login(username,password)){
            return myRequest.getMethod()+ myRequest.getUrl()+username+"\n登录成功";

        }else{
            return "登录失败";
        }

    }


    @MyRequestMapping("/admin")
    public void adminMainPage(MyRequest myRequest, MyResponse myResponse) throws IOException {
        testService.cycle();

    }


    @MyRequestMapping("/testt")
    public ArrayList<Car> sayHello(){
        return carList;
    }



}
