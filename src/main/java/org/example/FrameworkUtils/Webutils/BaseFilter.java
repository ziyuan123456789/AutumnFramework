package org.example.FrameworkUtils.Webutils;

/**
 * @author wangzhiyi
 * @since 2023.10
 */
public class BaseFilter implements Filter {
    @Override
    public void setNextHandler(Filter filter) {

    }

    @Override
    public boolean doChain(Request request) {
        return false;
    }
}
