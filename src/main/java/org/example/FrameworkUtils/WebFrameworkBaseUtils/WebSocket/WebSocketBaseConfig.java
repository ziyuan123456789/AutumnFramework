package org.example.FrameworkUtils.WebFrameworkBaseUtils.WebSocket;

import java.io.IOException;

/**
 * @author ziyuan
 * @since 2024.04
 */
public interface WebSocketBaseConfig {

    public void onOpen() ;


    public void onClose();

    public String onMsg(String text);
}
