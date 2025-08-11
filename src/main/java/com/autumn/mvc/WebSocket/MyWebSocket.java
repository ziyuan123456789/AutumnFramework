package com.autumn.mvc.WebSocket;

/**
 * @author ziyuan
 * @since 2024.04
 */
public class MyWebSocket {

    private static final MyWebSocket INSTANCE = new MyWebSocket();

    private MyWebSocket() {

    }

    public static MyWebSocket getInstance() {
        return MyWebSocket.INSTANCE;
    }
}
