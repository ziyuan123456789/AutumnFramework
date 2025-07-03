package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPostConstruct;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.AutumnCore.Aop.RequestContext;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.EnvironmentAware;
import org.example.FrameworkUtils.AutumnCore.compare.AnnotationInterfaceAwareOrderComparator;
import org.example.FrameworkUtils.AutumnCore.env.Environment;
import org.example.FrameworkUtils.DataStructure.MethodWrapper;
import org.example.FrameworkUtils.DataStructure.Tuple;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector.ControllerInjector;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector.Injector;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Icon;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseWriter.TomCatHtmlResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

@Slf4j
@WebServlet("/*")
@MyComponent
public class DispatcherServlet extends HttpServlet implements BeanFactoryAware, EnvironmentAware {

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final AnnotationInterfaceAwareOrderComparator comparator = AnnotationInterfaceAwareOrderComparator.getInstance();

    private ApplicationContext context;

    private Environment environment;

    @MyAutoWired
    private TomCatHtmlResponse tomCatHtmlResponse;

    @MyAutoWired
    private ObjectMapper objectMapper;

    private Set<ControllerInjector> controllerInjectors = new HashSet<>();

    private ExecutorService threadPool;

    private final List<Filter> filters = new ArrayList<>();

    private final TrieTree tree = new TrieTree();


    /**
     * 已使用前缀树重构,未来将支持PostMapping/GetMapping等注解
     */
    @MyPostConstruct
    private void initUrlMapping() {
        for (MyBeanDefinition mb : context.getBeanDefinitionMap().values()) {
            Class<?> clazz = mb.getBeanClass();
            if (clazz.isAnnotationPresent(Injector.class)) {
                controllerInjectors.add((ControllerInjector) context.getBean(mb.getName()));
            }

            if (Filter.class.isAssignableFrom(clazz)) {
                filters.add((Filter) context.getBean(mb.getName()));
            }
            String baseUrl = "";
            if (clazz.isAnnotationPresent(MyController.class)) {
                if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                    baseUrl = clazz.getAnnotation(MyRequestMapping.class).value();
                }
                for (Method method : clazz.getDeclaredMethods()) {
                    MyRequestMapping myRequestMapping = method.getAnnotation(MyRequestMapping.class);
                    if (myRequestMapping != null) {
                        String url = myRequestMapping.value();
                        String fullUrl = baseUrl + url;
                        tree.insert(fullUrl, new MethodWrapper(method, mb.getName()));
                    }
                }

            }
        }
        comparator.compare(filters);
        context.addBean("urlMappingTree", tree);
    }


    @Override
    public void init() throws ServletException {
        super.init();
        int threadNums = 10;
        threadPool = Executors.newFixedThreadPool(threadNums);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            AutumnRequest autumnRequest = new HttpServletRequestAdapter(req);
            RequestContext.setRequest(autumnRequest);
            AutumnResponse autumnResponse = new ServletResponseAdapter(resp, (TomCatHtmlResponse) context.getBean(TomCatHtmlResponse.class.getName()));
            String url = autumnRequest.getUrl();
            String baseUrl = extractPath(url);
            MethodWrapper methodWrapper = tree.search(baseUrl);
            if (methodWrapper != null) {
                executeHandler(autumnRequest, autumnResponse, resp, methodWrapper);
            } else {
                if (!resp.isCommitted()) {
                    resp.sendRedirect("/404");
                } else {
                    log.error("重定向到/404");
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
    public void destroy() {
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
        log.error(message, e);
    }

    private void executeHandler(AutumnRequest req, AutumnResponse res, HttpServletResponse resp, MethodWrapper handlerMethod) {
        Filter filter = filters.get(0);
        if (!filter.doChain(req, res)) {
            try {
                Tuple<Object, Class<?>> result = invokeMethod(handlerMethod, req, res);
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
                log.error(e.getMessage(), e);
                Throwable cause = e.getCause();
                String errorMessage;
                if (cause != null) {
                    errorMessage = "内部服务器错误：原因 - " + cause.getMessage();
                } else {
                    errorMessage = "内部服务器错误：详细信息 - " + e.getMessage();
                }

                try {
                    tomCatHtmlResponse.outPutErrorMessageWriter(resp, null, 500, errorMessage, format.format(new Date()), null);
                } catch (IOException ioException) {
                    log.error("发送错误响应时出现IO异常", ioException);
                }
            }


        }

    }


    private Tuple<Object, Class<?>> invokeMethod(MethodWrapper wrapper, AutumnRequest myRequest, AutumnResponse myResponse)
            throws InvocationTargetException, IllegalAccessException {
        Method method = wrapper.getMethod();
        Object instance = context.getBean(wrapper.getBeanName());
        int paramCount = method.getParameterCount();
        Object[] objectArgs = new Object[paramCount];
        for (ControllerInjector injector : controllerInjectors) {
            injector.inject(wrapper, instance, objectArgs, myRequest, myResponse);
        }
        return new Tuple<>(method.invoke(instance, objectArgs), method.getReturnType());
    }

    private Object useUrlGetParam(String paramName, AutumnRequest myRequest) {
        Map<String, String> param = myRequest.getParameters();
        return param.get(paramName);
    }


    //xxx:依照方法的返回值来确定选择哪种返回器
    private void handleSocketOutputByType(Object result, HttpServletResponse resp) {
        try {
            if (result instanceof View) {
                tomCatHtmlResponse.outPutHtmlWriter(resp, ((View) result).getHtmlName(), null);
            } else if (result instanceof Icon) {
                tomCatHtmlResponse.outPutIconWriter(resp, ((Icon) result).getIconName(), null);
            } else if (result instanceof Map) {
                tomCatHtmlResponse.outPutMessageWriter(resp, 200, objectMapper.writeValueAsString(result), null);
            } else if (isPrimitiveOrWrapper(result.getClass())) {
                tomCatHtmlResponse.outPutMessageWriter(resp, 200, result.toString(), null);
            }
//            else if (result instanceof MyWebSocket) {
//                tomCatHtmlResponse.outPutSocketWriter(resp, myRequest.getBody(), myRequest.getUrl());
//            }
            else {
                tomCatHtmlResponse.outPutMessageWriter(resp, 200, objectMapper.writeValueAsString(result), null);
            }
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
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.context = beanFactory;
    }
}



