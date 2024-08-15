package org.example.FrameworkUtils.AutumnCore.Aop;

import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
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
}
