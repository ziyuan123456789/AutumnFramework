package org.example.FrameworkUtils.AutumnMVC;

import org.example.FrameworkUtils.Webutils.Request;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
public interface HandlerInterceptor {
    boolean preHandle(Request request);
    void postHandle(Request request);
    void afterCompletion(Request request);
}
