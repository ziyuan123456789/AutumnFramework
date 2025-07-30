package com.autumn.swaggerstarter;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.ContextRefreshedEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationListener;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.DispatcherServlet;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.ServletResponseAdapter;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.TrieTree;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;

/**
 * @author ziyuan
 * @since 2024.08
 */

/**
 * 一个错误的设计,但是好用.以后会改
 * 有句话怎么说的? 黑猫白猫,能抓到耗子的就是好猫
 * TODO:日后通过getBean(TrieTree)方式修改
 */
@MyController
public class BaseController implements ApplicationListener<ContextRefreshedEvent> {

    @MyAutoWired
    private DispatcherServlet dispatcherServlet;

    private TrieTree tree;

    @MyRequestMapping("/myswagger")
    public View myswagger() {
        return new View("MySwagger.html");
    }
    @MyRequestMapping("/ReactPage")
    public View minireactPage() {
        return new View("minireact.html");
    }

    @MyRequestMapping("/get-new-swagger")
    public void getNewSwagger(AutumnResponse response) {
        ServletResponseAdapter servletResponseAdapter = (ServletResponseAdapter) response;
        servletResponseAdapter.outputJavaScriptFile("js/newswagger.js");
    }

    @MyRequestMapping("/getapp")
    public void getAppJs(AutumnResponse response) {
        ServletResponseAdapter servletResponseAdapter = (ServletResponseAdapter) response;
        servletResponseAdapter.outputJavaScriptFile("js/app.js");
    }

    @MyRequestMapping("/getminireact")
    public void getMinireactJs(AutumnResponse response) {
        ServletResponseAdapter servletResponseAdapter=(ServletResponseAdapter) response;
        servletResponseAdapter.getResponse().setContentType("application/javascript;charset=UTF-8");
        servletResponseAdapter.outputJavaScriptFile("js/minireact.js");
    }

    @MyRequestMapping("/getecharts")
    public void getEchartsJs(AutumnResponse response) {
        ServletResponseAdapter servletResponseAdapter = (ServletResponseAdapter) response;
        servletResponseAdapter.outputJavaScriptFile("js/echarts.min.js");
    }


    @MyRequestMapping("/urlMapping")
    public TrieTree urlMapping() {
        return tree;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        tree = dispatcherServlet.getTree();
    }

    @Override
    public boolean supportsEvent(ApplicationEvent event) {
        return event instanceof ContextRefreshedEvent;
    }
}
