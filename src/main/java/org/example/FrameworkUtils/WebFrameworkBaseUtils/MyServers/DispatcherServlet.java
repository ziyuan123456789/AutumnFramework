package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;


import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Aop.RequestContext;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnBeanFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.DataStructure.Tuple;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector.ControllerInjector;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector.Injector;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Json.JsonFormatter;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Icon;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseWriter.TomCatHtmlResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ziyuan
 * @since 2024.05
 */
@WebServlet("/*")
@Slf4j

@MyComponent
public class DispatcherServlet extends HttpServlet implements BeanFactoryAware {
    private AutumnBeanFactory myContext;
    @MyAutoWired
    private TomCatHtmlResponse tomCatHtmlResponse;
    @MyAutoWired
    private JsonFormatter jsonFormatter;


    @MyAutoWired
    private AnnotationScanner scanner;

    private Set<ControllerInjector> controllerInjectors = new HashSet<>();

    private ExecutorService threadPool;

    private Map<String, String> handlerMap;


    @Override
    public void init() throws ServletException {
        super.init();
        int threadNums = 10;
        Set<Class<?>> temp=scanner.findAnnotatedClassesList((String) myContext.get("packageUrl"), List.of(Injector.class));
        for(Class c:temp){
            try {
                controllerInjectors.add((ControllerInjector) c.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        handlerMap = (Map<String, String>) myContext.get("urlmapping");
        threadPool = Executors.newFixedThreadPool(threadNums);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            AutumnRequest autumnRequest = new HttpServletRequestAdapter(req);
            RequestContext.setRequest(autumnRequest);
            AutumnResponse autumnResponse = new ServletResponseAdapter(resp,(TomCatHtmlResponse) myContext.getBean(TomCatHtmlResponse.class.getName()));
            String url = autumnRequest.getUrl();
            String baseUrl = extractPath(url);
            String methodFullName = handlerMap.get(baseUrl);

            if (methodFullName != null) {
                executeHandler(autumnRequest, autumnResponse, resp, methodFullName);
            } else {
                if (!resp.isCommitted()) {
                    resp.sendRedirect("/404");
                } else {
                    log.error("Response already committed. Unable to redirect to /404.");
                }
            }
        } catch (Exception e) {
            log.error("Error processing request", e);
            if (!resp.isCommitted()) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
            }
        }
    }
    @Override
    public void destroy()
    {
        RequestContext.clear();
        log.info("请求对象生命周期结束");
    }


    private String extractPath(String url) {
        int questionMarkIndex = url.indexOf('?');
        if (questionMarkIndex != -1) {
            return url.substring(0, questionMarkIndex);
        } else {
            return url;
        }
    }

    //xxx:输出异常信息
    private <T extends Exception> void exceptionPrinter(T e, String message) {
        e.printStackTrace();
        log.error(message, e);
    }

    private void executeHandler(AutumnRequest req, AutumnResponse autumnResponse, HttpServletResponse resp, String handlerMethod) throws Exception {
        Filter filter = (Filter) myContext.getBean(AnnotationScanner.initFilterChain().getName());
        if (!filter.doChain(req, autumnResponse)) {
            int lastIndex = handlerMethod.lastIndexOf(".");
            String classurl = handlerMethod.substring(0, lastIndex);
            String methodName = handlerMethod.substring(lastIndex + 1);
            try {
                Tuple<Object, Class<?>> result = invokeMethod(classurl, methodName, req, autumnResponse);
                if (result.second.equals(void.class)) {
                    return;
                }
                if (result.first != null) {
                    handleSocketOutputByType(result.first, resp);
                } else {
                    resp.setContentType("text/html;charset=UTF-8");
                    resp.getWriter().write("");
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
            } catch (Exception e) {
                log.error("处理请求时出现异常: {}", methodName, e);
                Throwable cause = e.getCause();
                String errorMessage;
                if (cause != null) {
                    errorMessage = "内部服务器错误：原因 - " + cause.getMessage();
                } else {
                    errorMessage = "内部服务器错误：详细信息 - " + e.getMessage();
                }

                try {
                    tomCatHtmlResponse.outPutErrorMessageWriter(resp, 500, errorMessage, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), null);
                } catch (IOException ioException) {
                    log.error("发送错误响应时出现IO异常", ioException);
                }
            }


        }

    }


    private Tuple<Object, Class<?>> invokeMethod(String classurl, String methodName, AutumnRequest myRequest, AutumnResponse myResponse) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = Class.forName(classurl);
        Object instance = myContext.getBean(clazz.getName());
        Method domethod = null;
        List<Object> objectList = new ArrayList<>();
        if (classurl.contains("$$")) {
            clazz = clazz.getSuperclass();
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                domethod = method;
                for(ControllerInjector c: controllerInjectors){
                    c.inject(method, instance, objectList, myRequest, myResponse);
                }
            }
        }
        return new Tuple<>(domethod.invoke(instance, objectList.toArray()), domethod.getReturnType());
    }

    private Object useUrlGetParam(String paramName, AutumnRequest myRequest) {
        Map<String, String> param = myRequest.getParameters();
        return param.get(paramName);
    }

    private static boolean isJavaBean(Class<?> clazz) {
        try {
            clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            return false;
        }
        if (Collection.class.isAssignableFrom(clazz) ||
                Map.class.isAssignableFrom(clazz) ||
                clazz.isArray() ||
                clazz.isPrimitive() ||
                clazz == String.class ||
                Number.class.isAssignableFrom(clazz) ||
                Date.class.isAssignableFrom(clazz)) {
            return false;
        }

        return true;
    }

    //xxx:依照方法的返回值来确定选择哪种返回器
    private void handleSocketOutputByType(Object result, HttpServletResponse resp) {
        try {
            if (result instanceof View) {
                tomCatHtmlResponse.outPutHtmlWriter(resp, ((View) result).getHtmlName(), null);
            } else if (result instanceof Icon) {
                tomCatHtmlResponse.outPutIconWriter(resp, ((Icon) result).getIconName(), null);
            } else if (result instanceof Map) {
                tomCatHtmlResponse.outPutMessageWriter(resp, 200, jsonFormatter.toJson(result), null);
            } else if (isPrimitiveOrWrapper(result.getClass())) {
                tomCatHtmlResponse.outPutMessageWriter(resp, 200, result.toString(), null);
            }
//            else if (result instanceof MyWebSocket) {
//                tomCatHtmlResponse.outPutSocketWriter(resp, myRequest.getBody(), myRequest.getUrl());
//            }
            else {
                tomCatHtmlResponse.outPutMessageWriter(resp, 200, jsonFormatter.toJson(result), null);
            }
        } catch (IOException e) {

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> classType) {
        return classType.isPrimitive() ||
                classType.equals(String.class) ||
                classType.equals(Boolean.class) ||
                classType.equals(Integer.class) ||
                classType.equals(Character.class) ||
                classType.equals(Byte.class) ||
                classType.equals(Short.class) ||
                classType.equals(Double.class) ||
                classType.equals(Long.class) ||
                classType.equals(Float.class);
    }


    @Override
    public void setBeanFactory(AutumnBeanFactory beanFactory) {
        this.myContext = beanFactory;
    }
}



