package org.example.FrameworkUtils.Exception;

import java.net.Socket;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
public interface ExceptionHandler {
    void handleException(Exception e, Socket socket);
}
