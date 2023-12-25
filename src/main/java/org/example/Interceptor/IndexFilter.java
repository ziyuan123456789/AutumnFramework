package org.example.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyComponent;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Filter;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Request;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Response;

/**
 * @author ziyuan
 * @since 2023.10
 */
@MyComponent
@Slf4j
public class IndexFilter implements Filter {

    @Override
    public boolean doChain(Request request, Response response) {
        return false;
    }
}
