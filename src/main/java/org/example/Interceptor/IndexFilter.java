package org.example.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Webutils.Filter;
import org.example.FrameworkUtils.Webutils.Request;

/**
 * @author ziyuan
 * @since 2023.10
 */
@MyComponent
@Slf4j
public class IndexFilter implements Filter {

    @Override
    public boolean doChain(Request request) {
        return false;
    }
}
