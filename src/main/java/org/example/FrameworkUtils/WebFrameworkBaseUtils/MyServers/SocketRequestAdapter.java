package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import org.example.FrameworkUtils.WebFrameworkBaseUtils.Cookie.Cookie;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Session.MySession;

import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.05
 */
@Deprecated
public class SocketRequestAdapter implements AutumnRequest {
    private final MyRequest originalRequest;

    public SocketRequestAdapter(MyRequest request) {
        this.originalRequest = request;
    }

    @Override
    public String getUrl() {
        return originalRequest.getUrl();
    }

    @Override
    public String getMethod() {
        return originalRequest.getMethod();
    }

    @Override
    public String getBody() {
        return originalRequest.getBody();
    }

    @Override
    public Map<String, String> getParameters() {
        return originalRequest.getParameters();
    }

    @Override
    public Cookie[] getCookies() {
        return originalRequest.getCookies();
    }

    @Override
    public Cookie getCookieByName(String name) {
        return originalRequest.getCookieByName(name);
    }

    @Override
    public MySession getSession() {
        return originalRequest.getSession();
    }
}
