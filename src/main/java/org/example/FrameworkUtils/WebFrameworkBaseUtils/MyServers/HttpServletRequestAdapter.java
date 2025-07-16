package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Cookie.Cookie;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Session.HttpSessionAdapter;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Session.MySession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.05
 */
@Slf4j
public class HttpServletRequestAdapter implements AutumnRequest {

    private final HttpServletRequest originalRequest;

    public HttpServletRequestAdapter(HttpServletRequest request) {
        this.originalRequest = request;
    }

    @Override
    public String getUrl() {
        String requestURI = originalRequest.getRequestURI();
        String queryString = originalRequest.getQueryString();
        if (queryString == null) {
            return requestURI;
        } else {
            return requestURI + '?' + queryString;
        }
    }


    @Override
    public String getMethod() {
        return originalRequest.getMethod();
    }

    @Override
    public String getBody() {
        return extractBody(originalRequest);
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String[]> originalParams = originalRequest.getParameterMap();
        Map<String, String> params = new HashMap<>();
        originalParams.forEach((key, values) -> {
            if (values != null && values.length > 0) {
                params.put(key, values[0]);
            }
        });
        return params;
    }

    @Override
    public Cookie[] getCookies() {
        return CookieConverter.convert(originalRequest.getCookies());
    }

    @Override
    public Cookie getCookieByName(String name) {
        javax.servlet.http.Cookie[] servletCookies = originalRequest.getCookies();
        if (servletCookies != null) {
            for (javax.servlet.http.Cookie servletCookie : servletCookies) {
                if (servletCookie.getName().equals(name)) {
                    Cookie myCookie = new Cookie(servletCookie.getName(), servletCookie.getValue());
                    myCookie.setMaxAge(servletCookie.getMaxAge());
                    myCookie.setPath(servletCookie.getPath());
                    return myCookie;
                }
            }
        }
        return null;
    }

    @Override
    public MySession getSession() {
        HttpSession session = originalRequest.getSession();
        if (session != null) {
            return new HttpSessionAdapter(session);
        }
        return null;
    }

    private String extractBody(HttpServletRequest request) {
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line).append('\n');
            }
        } catch (Exception e) {

        }
        return body.toString();
    }

    public static class CookieConverter {
        public static Cookie[] convert(javax.servlet.http.Cookie[] servletCookies) {
            if (servletCookies == null) {
                return new Cookie[0];
            }

            Cookie[] myCookies = new Cookie[servletCookies.length];
            for (int i = 0; i < servletCookies.length; i++) {
                javax.servlet.http.Cookie servletCookie = servletCookies[i];
                Cookie myCookie = new Cookie(servletCookie.getName(), servletCookie.getValue());
                myCookie.setMaxAge(servletCookie.getMaxAge());
                myCookie.setPath(servletCookie.getPath());
                myCookies[i] = myCookie;
            }

            return myCookies;
        }
    }

}


