package com.autumn.mvc.WebSocket;

/**
 * @author ziyuan
 * @since 2024.04
 */
public interface WebSocketEndpoint {

    void onOpen();


    void onClose();


    String onMsg(String text);
}
