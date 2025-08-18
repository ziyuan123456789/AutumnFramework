package org.example.FrameworkUtils.AutumnCore.Aop;

import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

/**
 * @author ziyuan
 * @since 2024.08
 */

// 这个类用于创建AutumnRequest和AutumnResponse的代理对象
public class AutumnRequestProxyFactory {

    public static AutumnRequest createAutumnRequestProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(AutumnRequest.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            AutumnRequest realRequest = RequestContext.getRequest();
            if (realRequest != null) {
                return method.invoke(realRequest, args);
            }
            throw new IllegalStateException("AutumnRequest创建失败");
        });
        return (AutumnRequest) enhancer.create();
    }

    public static AutumnResponse createAutumnResponseProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(AutumnResponse.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            AutumnResponse response = RequestContext.getResponse();
            if (response != null) {
                return method.invoke(response, args);
            }
            throw new IllegalStateException("AutumnResponse创建失败");
        });
        return (AutumnResponse) enhancer.create();
    }
}
