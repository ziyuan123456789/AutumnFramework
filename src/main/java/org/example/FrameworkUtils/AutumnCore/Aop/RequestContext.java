package org.example.FrameworkUtils.AutumnCore.Aop;

import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;

/**
 * @author ziyuan
 * @since 2024.08
 */

/**
 * RequestContext是一个任劳任怨的类,他总是忍气吞声的接受各种Holder
 * 但可悲的是,clear有bug,永远无法触发
 * 你们最好不要惹RequestContext,要不然OOM来了你们一个跑不了
 */
public class RequestContext {
    private static final ThreadLocal<AutumnRequest> requestHolder = new ThreadLocal<>();
    private static final ThreadLocal<AutumnResponse> responseHolder = new ThreadLocal<>();

    public static void setRequest(AutumnRequest request) {
        requestHolder.set(request);
    }

    public static AutumnRequest getRequest() {
        return requestHolder.get();
    }

    public static void setResponse(AutumnResponse response) {
        responseHolder.set(response);
    }

    public static AutumnResponse getResponse() {
        return responseHolder.get();
    }

    public static void clear() {
        requestHolder.remove();
        responseHolder.remove();
    }
}
