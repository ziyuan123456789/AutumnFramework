package org.example.FrameworkUtils.AutumnCore.Aop;

import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

/**
 * @author ziyuan
 * @since 2024.08
 */
public class AutumnRequestProxyFactory {
    public static AutumnRequest createAutumnRequestProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(AutumnRequest.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            AutumnRequest realRequest = RequestContext.getRequest();
            if (realRequest != null) {
                return method.invoke(realRequest, args);
            }
            throw new IllegalStateException("No request bound to the current thread");
        });
        return (AutumnRequest) enhancer.create();
    }

    public static AutumnResponse createAutumnResponseProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(AutumnResponse.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            AutumnResponse real = RequestContext.getResponse();
            if (real != null) {
                return method.invoke(real, args);
            }
            throw new IllegalStateException("No request bound to the current thread");
        });
        return (AutumnResponse) enhancer.create();
    }
}
