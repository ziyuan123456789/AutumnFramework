package org.example.FrameworkUtils.Exception;

import java.net.Socket;

/**
 * @author ziyuan
 * @since 2023.11
 */

public class ServerError500Exception implements ExceptionHandler {
    @Override
    public void handleException(Exception e, Socket socket) {

    }

}
