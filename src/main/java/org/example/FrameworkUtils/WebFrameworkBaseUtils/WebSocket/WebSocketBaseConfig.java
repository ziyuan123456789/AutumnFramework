package org.example.FrameworkUtils.WebFrameworkBaseUtils.WebSocket;

/**
 * @author ziyuan
 * @since 2024.04
 */
public interface WebSocketBaseConfig {

    void onOpen();


    void onClose();

    String onMsg(String text);
}
