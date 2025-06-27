package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import org.example.FrameworkUtils.AutumnCore.Aop.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @author ziyuan
 * @since 2025.06
 */
public class ResourceCleanupFilter implements javax.servlet.Filter {

    private static final Logger log = LoggerFactory.getLogger(ResourceCleanupFilter.class);


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } finally {
            RequestContext.clear();
            log.info("请求对象生命周期结束,成功清理RequestContext中的资源与ThreadLocal");
        }
    }

}
