package org.example.controller;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.example.Annotations.CheckParameter;
import org.example.Aop.UserAopProxyFactory;
import org.example.Bean.Temp;
import org.example.FrameworkUtils.Annotation.EnableAop;
import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Annotation.MyController;
import org.example.FrameworkUtils.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.Annotation.MyRequestParam;
import org.example.FrameworkUtils.Annotation.MyService;
import org.example.FrameworkUtils.Annotation.Value;
import org.example.FrameworkUtils.Controller.AutumnBaseController;
import org.example.FrameworkUtils.ResponseType.Views.View;
import org.example.FrameworkUtils.Webutils.Json.JsonFormatter;
import org.example.FrameworkUtils.Webutils.MyContext;
import org.example.FrameworkUtils.Webutils.Request;
import org.example.service.LoginService;
import org.example.service.TestService;

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
    JsonFormatter jsonFormatter;
    @MyAutoWired
    Temp t;
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

    @MyRequestMapping("/cglib")
    public String cglib(Request request) {
        Map<String,String> map = new HashMap<>();
        map.put("name","cglib");
        map.put("password","42");
        map.put("qdsa","714");

        return jsonFormatter.mapTojson(map);
    }
    @MyRequestMapping("/testt")
    public String sayHello(){
        return sqlUrl;
    }



}
