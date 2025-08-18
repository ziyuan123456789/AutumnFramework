package com.autumn.mvc.WebSocket;

/**
 * @author ziyuan
 * @since 2024.04
 */

/**
 * 废弃
 * 留给socketServer使用,用于兼容旧版本
 * 在旧版本中,SocketWebServer使用原生Socket实现了一个简单的HTTP服务器,再次基础上继续拓展了对WebSocket的支持
 */
@Deprecated
public class MyWebSocket {

    private static final MyWebSocket INSTANCE = new MyWebSocket();

    private MyWebSocket() {

    }

    public static MyWebSocket getInstance() {
        return MyWebSocket.INSTANCE;
    }
}
