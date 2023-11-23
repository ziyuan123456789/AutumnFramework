package org.example.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Annotation.MyOrder;
import org.example.FrameworkUtils.Webutils.Filter;
import org.example.FrameworkUtils.Webutils.Request;

/**
 * @author wsh
 */
@Slf4j
@MyComponent
@MyOrder(1)
public class UrlFilter implements Filter {
    private Filter nextFilter;
    @MyAutoWired
    IndexFilter indexFilter;

    @Override
    public void setNextHandler(Filter filter) {
        this.nextFilter = indexFilter;
    }

    @Override
    public boolean doChain(Request request) {
        if (request.getMethod().equals("GET")) {
            log.error("一级过滤链拦截,开始第一步鉴权");
            setNextHandler(indexFilter);
            return nextFilter.doChain(request);
        } else {
            log.error("一级过滤链放行");
            return false;
        }
    }
}
