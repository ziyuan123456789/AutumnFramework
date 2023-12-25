package org.example.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyComponent;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Filter;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyResponse;

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
