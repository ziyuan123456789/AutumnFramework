package org.example.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyOrder;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Filter;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Request;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Response;

/**
 * @author wsh
 */
@Slf4j
@MyComponent
@MyOrder(1)
public class UrlFilter implements Filter {
    @MyAutoWired
    IndexFilter indexFilter;

    @Override
    public boolean doChain(Request request, Response response) {
        if ("GET".equals(request.getMethod())) {
            log.info("一级过滤链拦截,开始第一步鉴权");
//            response.setCode(500).setResponseText("鉴权失败").outputMessage();
            return indexFilter.doChain(request, response);
        } else {
            log.info("一级过滤链放行");
            return false;
        }
    }
}
