package org.example.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Webutils.Filter;
import org.example.FrameworkUtils.Webutils.Request;

/**
 * @author wangzhiyi
 * @since 2023.10
 */
@MyComponent
@Slf4j
public class IndexFilter implements Filter {
    private Filter filter;

    @Override
    public void setNextHandler(Filter filter) {
        this.filter = filter;
    }

    @Override
    public boolean doChain(Request request) {
        return false;
//        if (request.getParameters().get("admin") == null) {
//            log.error("第二过滤链拦截");
//            return true;
//        } else {
//            log.error("二级过滤链放行");
//            return false;
//        }
    }
}
