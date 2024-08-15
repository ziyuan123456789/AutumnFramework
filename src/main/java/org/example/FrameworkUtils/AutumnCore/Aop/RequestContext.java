package org.example.FrameworkUtils.AutumnCore.Aop;

import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;

/**
 * @author ziyuan
 * @since 2024.08
 */
public class RequestContext {
    private static final ThreadLocal<AutumnRequest> requestHolder = new ThreadLocal<>();

    public static void setRequest(AutumnRequest request) {
        requestHolder.set(request);
    }

    public static AutumnRequest getRequest() {
        return requestHolder.get();
    }

    public static void clear() {
        requestHolder.remove();
    }
}
