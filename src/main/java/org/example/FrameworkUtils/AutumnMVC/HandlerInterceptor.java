package org.example.FrameworkUtils.AutumnMVC;

import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyRequest;

/**
 * @author ziyuan
 * @since 2023.11
 */
public interface HandlerInterceptor {
    boolean preHandle(MyRequest myRequest);
    void postHandle(MyRequest myRequest);
    void afterCompletion(MyRequest myRequest);
}
