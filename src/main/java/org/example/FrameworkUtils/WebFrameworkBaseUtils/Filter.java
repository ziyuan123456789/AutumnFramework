package org.example.FrameworkUtils.WebFrameworkBaseUtils;

import org.example.FrameworkUtils.WebFrameworkBaseUtils.Request;

/**
 * @author ziyuan
 * @since 2023.10
 */
public interface Filter {

    //xxx:拦截逻辑
    boolean doChain(Request request,Response response);
}
