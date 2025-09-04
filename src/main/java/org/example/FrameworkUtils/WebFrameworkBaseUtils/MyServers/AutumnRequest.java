package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import org.example.FrameworkUtils.WebFrameworkBaseUtils.Cookie.Cookie;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Session.MySession;

import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.05
 */
public interface AutumnRequest {
    String getUrl();

    String getMethod();

    String getBody();

    Map<String, String> getParameters();

    Cookie[] getCookies();

    Cookie getCookieByName(String name);

    MySession getSession();
}
