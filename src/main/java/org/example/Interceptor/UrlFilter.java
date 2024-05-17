package org.example.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyOrder;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.Filter;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.MyRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.MyResponse;

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
//            myResponse.setCode(400).setResponseText("鉴权失败").outputErrorMessage();
            return indexFilter.doChain(myRequest, myResponse);
        } else {
            log.info("一级过滤链放行");
            return false;
        }
    }
}
