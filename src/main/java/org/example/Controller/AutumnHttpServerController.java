package org.example.Controller;

import com.autumn.mvc.AutumnNotBlank;
import com.autumn.mvc.ErrorHandler;
import com.autumn.mvc.GetMapping;
import com.autumn.mvc.PathVariable;
import com.autumn.mvc.PostMapping;
import com.autumn.mvc.SessionAttribute;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.Lazy;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.Orm.MyRedis.MyRedisTemplate;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Cookie.Cookie;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;

/**
 * @author ziyuan
 * @since 2025.06
 */
@Slf4j
@MyController
@MyRequestMapping("/api")
public class AutumnHttpServerController {


    @MyAutoWired
    @Lazy
    private MyRedisTemplate myRedisTemplate;


    @MyAutoWired
    private AutumnRequest autumnRequest;

    @MyAutoWired
    private AutumnResponse autumnResponse;

    //测试GetMapping
    @GetMapping
    @MyRequestMapping("/getMappingTest")
    public String getMappingTest() {
        return "GetMapping测试成功";
    }

    //测试PostMapping
    @PostMapping
    @MyRequestMapping("/postMappingTest")
    public String postMappingTest() {
        return "PostMapping测试成功";
    }

    //测试session功能
    @MyRequestMapping("/")
    public String setSession(AutumnRequest myRequest) {
        String sessionId = myRequest.getSession().getSessionId();
        myRequest.getSession().setAttribute("id", sessionId);
        return "切换阅览器查看唯一标识符是否变化? 标识符如下:" + myRequest.getSession().getAttribute("id");
    }


    //测试sessionAttribute
    @MyRequestMapping("/attribute")
    public String sessionAttributeTest(@SessionAttribute(name = "id") String id) {
        return id;
    }


    //测试路径传参
    @MyRequestMapping("/test/{sn}")
    public String pathVariable(@PathVariable("sn") String sn) {
        return sn;
    }

    //测试参数注入
    @MyRequestMapping("/paramTest")
    public String paramTest(String name, String age) {
        return name + "+" + age;
    }


    //测试ErrorHandler以及AutumnNotBlank与AutumnNotBlank
    @ErrorHandler(errorCode = 400, title = "参数校验异常")
    @MyRequestMapping("/notnull")
    public String notNullOrBlankTest(@AutumnNotBlank String id, @AutumnNotBlank String name) {
        return id + "+" + name;
    }

    //测试全局request功能
    @MyRequestMapping("/request")
    public String requestTestWithField() {
        log.info(myRedisTemplate.getClass().toString());
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


    //测试View层功能
    @MyRequestMapping("/html")
    public View myhtml() {
        return new View("AutumnFrameworkMainPage.html");
    }


}
