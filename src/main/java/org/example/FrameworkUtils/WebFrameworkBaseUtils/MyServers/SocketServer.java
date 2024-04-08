package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.*;
import org.example.FrameworkUtils.AutumnMVC.AnnotationScanner;
import org.example.FrameworkUtils.AutumnMVC.MyMultipartFile;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Cookie.Cookie;
import org.example.FrameworkUtils.DataStructure.Tuple;
import org.example.FrameworkUtils.Exception.NoAvailableUrlMappingException;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyResponse;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.ConditionCheck.SocketServerConditionCheck;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Icon;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseWriter.HtmlResponse;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Session.MySession;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Session.SessionManager;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Filter;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Json.JsonFormatter;
import org.example.FrameworkUtils.AutumnMVC.MyContext;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.WebSocket.MyWebSocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * @author ziyuan
 * @since 2023.10
 */
@Slf4j
@MyComponent
@MyConditional(SocketServerConditionCheck.class)
public class SocketServer implements MyServer {
    private ExecutorService threadPool;
    private ServerSocket serverSocket;
    private final MyContext myContext = MyContext.getInstance();
    private final SessionManager sessionmanager = myContext.getBean(SessionManager.class);
    @MyAutoWired
    HtmlResponse htmlResponse;
    @MyAutoWired
    AnnotationScanner annotationScanner;
    @MyAutoWired
    JsonFormatter jsonFormatter;
    @Value("port")
    private Integer port;
    @Value("threadPoolNums")
    private Integer threadNums;


    @Override
    public void init() throws Exception {
        threadPool = Executors.newFixedThreadPool(threadNums);
        Map<String, String> sharedMap = (Map<String, String>) myContext.get("urlmapping");
        try {
            serverSocket = new ServerSocket(port);
            log.info("服务于" + port + "端口启动");
            log.info("http://localhost:" + port + "/");
            while (!serverSocket.isClosed()) {
                final Socket clientSocket = serverSocket.accept();
                InputStream is = clientSocket.getInputStream();

                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder headerBuilder = new StringBuilder();
                String line;

                String contentType="";
                String boundary="";
                int contentLength = 2048;
                while ((line = reader.readLine()) != null && !line.isEmpty()) {


                    headerBuilder.append(line).append("\n");
                    if (line.startsWith("GET")) {
                       break;
                    }
                    if (line.startsWith("Content-Length: ")) {
                        contentLength = Integer.parseInt(line.substring("Content-Length: ".length()).trim());
                    }
                    if(line.startsWith("Content-Type: ")){
                        String[] parts = line.split(";");
                        contentType = parts[0].trim();
                        for (String part : parts) {
                            if (part.trim().startsWith("boundary=")) {
                                boundary = part.trim().substring("boundary=".length());
                                break;
                            }
                        }

                    }


                }
                if (contentLength == -1) {
                    htmlResponse.outPutMessageWriter(clientSocket, 500, "POST方法你不带长度?", null);
                }
                char[] bodyChars = new char[contentLength];
                int charsRead = reader.read(bodyChars);
                String requestBody = new String(bodyChars, 0, charsRead);
                int finalContentLength = contentLength;
                String finalContentType = contentType;
                String finalBoundary = boundary;
                threadPool.execute(() -> {
                    try {
                        processRequest(clientSocket, sharedMap, String.valueOf(headerBuilder),requestBody, finalContentLength, finalContentType, finalBoundary);
                    } catch (IOException e) {
                        exceptionPrinter(e, "IO异常");
                    } catch (NoAvailableUrlMappingException e) {
                        exceptionPrinter(e, "没有可用的映射表,请在控制器加入需要映射的url-method");
                    } catch (Exception e) {
                        exceptionPrinter(e, "未知异常");
                    }
                });
            }


        } catch (BindException e) {
            log.error(e.toString());
            log.error("""
                    
                    
                    ***************************
                    APPLICATION FAILED TO START
                    ***************************
                                        
                    Description:
                                        
                    网络服务启动失败,端口被占用.
                                        
                                        """);
        }catch (Exception e){
            serverSocket.close();
            log.error(e.toString());
        }
    }

    private Tuple<Object, Class<?>> invokeMethod(String classurl, String methodName, MyRequest myRequest, MyResponse myResponse) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = Class.forName(classurl);
        Object instance = myContext.getBean(clazz);
        Method domethod=null;
        List<Object> objectList = new ArrayList<>();
        if (classurl.contains("$$")) {
            clazz = clazz.getSuperclass();
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                domethod = method;
                Parameter[] parameters = method.getParameters();
                for (Parameter parameter : parameters) {
                    if(isJavaBean(parameter.getClass())){
                        log.info(parameter.getClass()+"是一个Javabean");
                    }
                    MyRequestParam myRequestParam = parameter.getAnnotation(MyRequestParam.class);
                    if (parameter.getType().equals(MyRequest.class)) {
                        objectList.add(myRequest);
                    }
                    if (parameter.getType().equals(MyMultipartFile.class)) {
                        objectList.add(myRequest.getMyMultipartFile());
                    }
                    if(parameter.getType().equals(MyResponse.class)){
                        objectList.add(myResponse);
                    }
                    if (myRequestParam != null) {
                        if (!myRequestParam.value().isEmpty()) {
                            objectList.add(useUrlGetParam(myRequestParam.value(), myRequest));
                        }
                    }
                }
            }
        }
        return new Tuple<>(domethod.invoke(instance, objectList.toArray()), domethod.getReturnType());
    }
    private Object useUrlGetParam(String paramName, MyRequest myRequest){
        Map<String,String> param= myRequest.getParameters();
        return param.get(paramName);
    }

    private void processRequest(Socket clientSocket, Map sharedMap,String payload,String body,Integer lenth,String contentType,String boundary) throws IOException, ClassNotFoundException {
            boolean urlmark = true;
            MyRequest myRequest = new MyRequest(payload,body,lenth);
            if("multipart/form-data".equals(contentType)){
                myRequest.setContentType("multipart/form-data");
                myRequest.setBoundary(boundary);
            }
            String baseurl = myRequest.getUrl();
            if (sharedMap != null) {
                String str = extractPath(baseurl);
                if (sharedMap.containsKey(str)) {
                    myRequest.setParameters(baseurl);
                    urlmark = false;
                    str = (String) sharedMap.get(str);
                    int lastIndex = str.lastIndexOf(".");
                    String classurl = str.substring(0, lastIndex);
                    Filter filter = (Filter) myContext.getBean(annotationScanner.initFilterChain());
                    MyResponse myResponse =new MyResponse(htmlResponse, clientSocket);
                    if (!filter.doChain(myRequest, myResponse)) {
                        String methodName = str.substring(lastIndex + 1);
                        try {
                            Tuple<Object, Class<?>> result = invokeMethod(classurl, methodName, myRequest, myResponse);
                            if (result.second.equals(void.class)) {
                                htmlResponse.outPutMessageWriter(clientSocket, 200, "", null);
                                return;
                            }
                            if (result.first != null) {
                                handleSocketOutputByType(clientSocket, result.first, myRequest);
                            } else {
                                htmlResponse.outPutMessageWriter(clientSocket, 200, "", null);
                            }

                        } catch (InvocationTargetException | ClassNotFoundException | NoSuchMethodException |
                                 IllegalAccessException | RuntimeException e) {
                            Throwable cause = e.getCause();
                            log.warn("异常来自" + methodName);
                            cause.printStackTrace(System.err);
                            htmlResponse.outPutErrorMessageWriter(clientSocket, 500, e.getCause().toString(),new Date().toString(), null);
                        }
                    }
                }
            } else {
                throw new NoAvailableUrlMappingException("空的映射表,请在controller上添加url映射");
            }
            if (urlmark) {
                log.warn(baseurl);
                log.warn("404");
                htmlResponse.redirectLocationWriter(clientSocket, "/404");
            }

        }

    //xxx:解析url
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

    //xxx:依照方法的返回值来确定选择哪种返回器
    private void handleSocketOutputByType(Socket clientSocket, Object result, MyRequest myRequest) throws IOException, IllegalAccessException {
        Cookie cookie = myRequest.getCookieByName("userSession");
        if (cookie != null) {
            cookie = null;
        } else {
            String uuid = String.valueOf(UUID.randomUUID());
            cookie = new Cookie("userSession", uuid);
            MySession newSession = new MySession(uuid);
            sessionmanager.getSessions().put(uuid, newSession);
        }

        if (result instanceof View) {
            htmlResponse.outPutHtmlWriter(clientSocket, ((View) result).getHtmlName(), cookie);
        } else if (result instanceof Icon) {
            htmlResponse.outPutIconWriter(clientSocket, ((Icon) result).getIconName(), cookie);
        } else if (result instanceof Map) {
            htmlResponse.outPutMessageWriter(clientSocket, 200, jsonFormatter.toJson(result), cookie);
        } else if (isPrimitiveOrWrapper(result.getClass())) {
            htmlResponse.outPutMessageWriter(clientSocket, 200, result.toString(), cookie);
        } else if(result instanceof MyWebSocket){
            htmlResponse.outPutSocketWriter(clientSocket,myRequest.getBody(),myRequest.getUrl() );
        }else{
            htmlResponse.outPutMessageWriter(clientSocket, 200, jsonFormatter.toJson(result), cookie);
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



}
