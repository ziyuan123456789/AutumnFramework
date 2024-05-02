package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

/**
 * @author ziyuan
 * @since 2023.10
 */
public interface Filter {

    //xxx:拦截逻辑
    boolean doChain(MyRequest myRequest, MyResponse myResponse);
}
