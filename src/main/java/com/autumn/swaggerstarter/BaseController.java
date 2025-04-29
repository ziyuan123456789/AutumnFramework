package com.autumn.swaggerstarter;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.ServletResponseAdapter;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.08
 */

/**
 * 一个错误的设计,但是好用.以后会改
 * 有句话怎么说的? 黑猫白猫,能抓到耗子的就是好猫
 */
@MyController
public class BaseController implements BeanFactoryAware {

    private ApplicationContext beanFactory;

    @MyRequestMapping("/myswagger")
    public View myswagger() {
        return new View("MySwagger.html");
    }
    @MyRequestMapping("/ReactPage")
    public View minireactPage() {
        return new View("minireact.html");
    }

    @MyRequestMapping("/getapp")
    public void getAppJs(AutumnResponse response) {
        ServletResponseAdapter servletResponseAdapter = (ServletResponseAdapter) response;
        servletResponseAdapter.outputJavaScriptFile("js/app.js");
    }

    @MyRequestMapping("/getminireact")
    public void getMinireactJs(AutumnResponse response) {
        ServletResponseAdapter servletResponseAdapter=(ServletResponseAdapter) response;
        servletResponseAdapter.outputJavaScriptFile("js/minireact.js");
    }

    @MyRequestMapping("/getecharts")
    public void getEchartsJs(AutumnResponse response) {
        ServletResponseAdapter servletResponseAdapter = (ServletResponseAdapter) response;
        servletResponseAdapter.outputJavaScriptFile("js/echarts.min.js");
    }





    @MyRequestMapping("/urlMapping")
    public Map<String, Object> urlMapping() {
        Map<String, Object> openApiMap = new HashMap<>();
        for (MyBeanDefinition mb : beanFactory.getBeanDefinitionMap().values()) {
            Class<?> clazz = mb.getBeanClass();
            if (clazz.isAnnotationPresent(MyController.class)) {
                for (Method method : clazz.getDeclaredMethods()) {
                    MyRequestMapping myRequestMapping = method.getAnnotation(MyRequestMapping.class);
                    if (myRequestMapping != null) {
                        String url = myRequestMapping.value();
                        if (url.startsWith("/")) {
                            List<String> methodsName = new ArrayList<>();
                            Class<?>[] paramTypes = method.getParameterTypes();
                            for (Class<?> param : paramTypes) {
                                methodsName.add(param.getSimpleName());
                            }
                            Class<?> returnType = method.getReturnType();
                            Map<String, Object> methodInfo = new HashMap<>();
                            methodInfo.put("parameters", methodsName);
                            methodInfo.put("returnType", returnType.getSimpleName());
                            openApiMap.put(url, methodInfo);
                        } else {
                            throw new RuntimeException("URL格式错误");
                        }
                    }
                }
            }
        }

        return openApiMap;
    }


    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.beanFactory = beanFactory;
    }
}
