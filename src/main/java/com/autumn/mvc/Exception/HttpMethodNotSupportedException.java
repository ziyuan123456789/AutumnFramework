package com.autumn.mvc.Exception;

/**
 * @author ziyuan
 * @since 2025.07
 */
public class HttpMethodNotSupportedException extends RuntimeException {
    public HttpMethodNotSupportedException(String message) {
        super(message);
    }
}
