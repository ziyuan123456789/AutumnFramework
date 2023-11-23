package org.example.FrameworkUtils.Webutils;

/**
 * @author wangzhiyi
 * @since 2023.10
 */
public interface Filter {
    //xxx:后继节点
    void setNextHandler(Filter filter);
    //xxx:逻辑
    boolean doChain(Request request);
}
