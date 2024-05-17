package org.example.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.Filter;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.MyRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.MyResponse;

/**
 * @author ziyuan
 * @since 2023.10
 */
@MyComponent
@Slf4j
public class IndexFilter implements Filter {

    @Override
    public boolean doChain(MyRequest myRequest, MyResponse myResponse) {
        return false;
    }
}
