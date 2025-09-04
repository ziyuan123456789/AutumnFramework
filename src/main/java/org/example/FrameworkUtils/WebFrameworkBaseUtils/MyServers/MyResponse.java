package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Cookie.Cookie;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseWriter.SocketServerHtmlResponse;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.List;

/**
 * @author ziyuan
 * @since 2023.12
 */
@Deprecated
@Slf4j
public class MyResponse implements AutumnResponse {

    private final Socket socket;

    private Cookie cookie;

    private SocketServerHtmlResponse socketServerHtmlResponse;

    private int httpCode = 200;

    private String responseText = "";

    private View view;

    public MyResponse(SocketServerHtmlResponse socketServerHtmlResponse, Socket socket) {
        this.socketServerHtmlResponse = socketServerHtmlResponse;
        this.socket = socket;
    }

    @Override
    public MyResponse setView(View view) {
        this.view = view;
        return this;
    }

    @Override
    public MyResponse setCookie(Cookie cookie) {
        this.cookie = cookie;
        return this;
    }

    @Override
    public MyResponse setCode(int code) {
        this.httpCode = code;
        return this;
    }

    @Override
    public MyResponse setResponseText(String responseText) {
        this.responseText = responseText;
        return this;
    }

    @Override
    public void outputMessage() {
        try {
            socketServerHtmlResponse.outPutMessageWriter(socket, httpCode, responseText, cookie);
        } catch (IOException e) {
            log.error("htmlResponse输出失败");
        }
    }

    @Override
    public void outputErrorMessage(String title, String text, int code, List<String> origins) {
        try {
            socketServerHtmlResponse.outPutErrorMessageWriter(socket, code, text, new Date().toString(), null);
        } catch (IOException e) {
            log.error("htmlResponse输出失败");
        }
    }

    @Override
    public void outputHtml() {
        try {
            socketServerHtmlResponse.outPutHtmlWriter(socket, view.getHtmlName(), cookie);
        } catch (IOException e) {
            log.error("htmlResponse输出失败");
        }
    }
}
