package org.example.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Ioc.Ordered;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.Filter;

/**
 * @author wsh
 */
@Slf4j
@MyComponent
public class UrlFilter implements Filter, Ordered {

    @MyAutoWired
    IndexFilter indexFilter;

    @Override
    public boolean doChain(AutumnRequest autumnRequest, AutumnResponse autumnResponse) {
        if ("GET".equals(autumnRequest.getMethod())) {
            log.info("一级过滤链拦截,开始第一步鉴权");
//            myResponse.setCode(401).setResponseText("鉴权失败").outputErrorMessage();
            return indexFilter.doChain(autumnRequest, autumnResponse);
        } else {
            log.info("一级过滤链放行");
            return false;
        }
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
