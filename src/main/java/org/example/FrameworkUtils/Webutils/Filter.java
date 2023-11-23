package org.example.FrameworkUtils.Webutils;

/**
 * @author wangzhiyi
 * @since 2023.10
 */
public interface Filter {
    //xxx:后继节点
    void setNextHandler(Filter filter);
    //xxx:拦截逻辑
    boolean doChain(Request request);
}
