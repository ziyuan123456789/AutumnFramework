package org.example.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyOrder;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Filter;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyResponse;

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
    public boolean doChain(MyRequest myRequest, MyResponse myResponse) {
        if ("GET".equals(myRequest.getMethod())) {
            log.info("一级过滤链拦截,开始第一步鉴权");
//            myResponse.setCode(500).setResponseText("鉴权失败").outputMessage();
            return indexFilter.doChain(myRequest, myResponse);
        } else {
            log.info("一级过滤链放行");
            return false;
        }
    }
}
