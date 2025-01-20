package org.example.FrameworkUtils.AutumnCore.Aop;

import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

/**
 * @author ziyuan
 * @since 2024.08
 */

/**
 * 这是一个代理工厂,代理模式就是摩西十诫,不用代理模式的Java人就是异端,必须烧掉
 * 既然每个请求都是一个个独立的线程,于是采用ThreadLocal来存储
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
            throw new IllegalStateException("AutumnRequest创建失败");
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
            throw new IllegalStateException("AutumnResponse创建失败");
        });
        return (AutumnResponse) enhancer.create();
    }
}
