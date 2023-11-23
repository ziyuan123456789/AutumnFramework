package org.example.FrameworkUtils.Exception;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
public class NoAvailableUrlMappingException extends RuntimeException{
    public NoAvailableUrlMappingException() {
        super("没有可用的映射表,请在控制器加入需要映射的url-method");
    }

    public NoAvailableUrlMappingException(String message) {
        super(message);
    }

}
