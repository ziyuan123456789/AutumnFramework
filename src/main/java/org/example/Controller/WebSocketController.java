package org.example.Controller;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.WebSocket.MyWebSocketConfig;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.WebSocket.WebSocketBaseConfig;

/**
 * @author ziyuan
 * @since 2024.04
 */

//暂时弃用
@Deprecated
@MyWebSocketConfig("/websocket")
@Slf4j
public class WebSocketController implements WebSocketBaseConfig {


    @Override
    public void onOpen() {
        log.warn("切换到WebSocket");
    }

    @Override
    public void onClose() {
        log.warn("用户离开");
    }

    @Override
    public String onMsg(String text) {
        log.info("接受的讯息为{}", text);
        return text;
    }
}
