package org.example.Controller;

import com.autumn.mvc.WebSocket.MyWebSocketEndpoint;
import com.autumn.mvc.WebSocket.WebSocketEndpoint;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ziyuan
 * @since 2024.04
 */

@Slf4j
@MyWebSocketEndpoint("/WebSocket")
public class WebSocketController implements WebSocketEndpoint {


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
