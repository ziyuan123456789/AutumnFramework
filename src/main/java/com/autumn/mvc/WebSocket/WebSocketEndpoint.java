package com.autumn.mvc.WebSocket;

/**
 * @author ziyuan
 * @since 2024.04
 */

/**
 * WebSocket端点接口,想要用WebSocket请实现这个接口,并打上注解
 * 注解如下:{@link MyWebSocketEndpoint}
 */
public interface WebSocketEndpoint {

    void onOpen();


    void onClose();


    String onMsg(String text);
}
