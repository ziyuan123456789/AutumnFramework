package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyContext;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Cookie.Cookie;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Session.MySession;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Session.SessionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author ziyuan
 * @since 2023.10
 */

//@SuppressWarnings("all")
@Slf4j
public class MyRequest {
    private final String payload;
    private final String body;
    private String method;
    private String url;

    private final Integer contentLength;
    private MyMultipartFile myMultipartFile;
    private String contentType;
    private String boundary;
    private final Cookie[] cookie;
    private MyContext myContext;
    private final SessionManager sessionManager = (SessionManager) myContext.getBean(SessionManager.class.getName());

    public MyRequest(String payload, String body, Integer contentLength, ApplicationContext myContext) {
        this.myContext = (MyContext) myContext;
        this.payload = payload;
        this.body = body;
        parseRequest(payload);
        cookie = extractCookie(body);
        if ("GET".equals(getMethod())) {
            this.contentLength = null;
        } else {
            this.contentLength = contentLength;
        }
    }
//    //xxx:文件上传专用
//    public MyRequest(String payload, String body, Integer contentLength, String contentType, String boundary) {
//        this(payload, body, contentLength);
//        this.contentType = contentType;
//        this.boundary = boundary;
//    }

    //xxx:解析HTTP方法与Url
    private void parseRequest(String payload) {
        try {
            String firstLine = payload.split("\n")[0];
            String[] parts = firstLine.split(" ");
            this.method = parts[0];
            this.url = parts.length > 1 ? parts[1] : "";
        } catch (Exception e) {
            log.error("解析HTTP方法与Url失败" + e.getMessage());
        }
    }

    //xxx:解析cookie依照userSession取得对应的session,没有就新建一个
    public MySession getSession() {
        Cookie userSessionCookie = getCookieByName("userSession");
        if (userSessionCookie != null) {
            return sessionManager.getSession(userSessionCookie.getCookieValue());
        }
        return sessionManager.getSession(String.valueOf(UUID.randomUUID()));
    }


    //xxx:解析Cookie
    private Cookie[] extractCookie(String httpRequest) {
        String cookieHeader = "Cookie: ";
        int start = httpRequest.indexOf(cookieHeader);
        if (start == -1) {
            return new Cookie[0];
        }
        int end = httpRequest.indexOf('\n', start);
        if (end == -1) {
            end = httpRequest.length();
        }
        String cookiesString = httpRequest.substring(start + cookieHeader.length(), end).trim();
        String[] cookiePairs = cookiesString.split(";\\s*");
        Cookie[] cookies = new Cookie[cookiePairs.length];

        for (int i = 0; i < cookiePairs.length; i++) {
            String[] nameValuePair = cookiePairs[i].split("=", 2);
            String name = nameValuePair[0].trim();
            String value = nameValuePair.length > 1 ? nameValuePair[1].trim() : "";
            cookies[i] = new Cookie(name, value);
        }

        return cookies;
    }

    public Cookie getCookieByName(String name){
        if (cookie != null) {
            for (Cookie c : cookie) {
                if (name.equals(c.getCookieName())) {
                    return c;
                }
            }
        }
        return null;
    }

    public  Map<String, String> getParameters() {
        Map<String, String> queryParams = new HashMap<>();
        if ("GET".equals(method)) {
            int questionMarkIndex = url.indexOf('?');
            if (questionMarkIndex != -1) {
                String queryString = url.substring(questionMarkIndex + 1);
                String[] paramPairs = queryString.split("&");
                for (String paramPair : paramPairs) {
                    String[] keyValue = paramPair.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = keyValue[1];
                        queryParams.put(key, value);
                    }
                }
            }
            return queryParams;
        } else {
            String[] paramPairs = body.split("&");
            for (String paramPair : paramPairs) {
                String[] keyValue = paramPair.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];
                    queryParams.put(key, value);
                }
            }

            return queryParams;
        }

    }

    public void setParameters(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public String getBody() {
        return body;
    }


    public Integer getContentLength() {
        return contentLength;
    }

    public MyMultipartFile getMyMultipartFile() {
        return myMultipartFile;
    }

    public void setMyMultipartFile(MyMultipartFile myMultipartFile) {
        this.myMultipartFile = myMultipartFile;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBoundary() {
        return boundary;
    }

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }


    public Cookie[] getCookies() {
        return cookie;
    }
}








