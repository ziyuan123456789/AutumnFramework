package org.example.FrameworkUtils.WebFrameworkBaseUtils.Session;

import javax.servlet.http.HttpSession;

/**
 * @author ziyuan
 * @since 2024.05
 */
public class HttpSessionAdapter extends MySession {
    private HttpSession httpSession;

    public HttpSessionAdapter(HttpSession session) {
        super(session.getId());
        this.httpSession = session;
    }

    @Override
    public void setAttribute(String key, Object value) {
        httpSession.setAttribute(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return httpSession.getAttribute(key);
    }

}