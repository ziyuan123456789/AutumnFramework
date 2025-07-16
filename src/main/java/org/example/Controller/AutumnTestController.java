package org.example.Controller;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.WebSocket.MyWebSocket;

/**
 * @author ziyuan
 * @since 2024.04
 */
@MyController
@Slf4j
public class AutumnTestController {

    //测试WebSocket功能,弃用
    @MyRequestMapping("/websocket")
    public MyWebSocket websocketTest() {
        return MyWebSocket.getInstance();
    }

}
