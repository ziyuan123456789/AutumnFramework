package org.example.FrameworkUtils.ResponseType;

import org.example.FrameworkUtils.Cookie.Cookie;
import org.example.FrameworkUtils.ResponseWriter.HtmlResponse;

import java.io.IOException;
import java.net.Socket;

/**
 * @author ziyuan
 * @since 2023.12
 */

public class Response {
    private final Socket socket;
    private Cookie cookie;
    private HtmlResponse htmlResponse;
    private int httpCode=200;
    private String responseText;

    public Response(HtmlResponse htmlResponse, Socket socket) {
        this.htmlResponse = htmlResponse;
        this.socket = socket;
    }

    public Response setCookie(Cookie cookie) {
        this.cookie = cookie;
        return this;
    }

    public Response setCode(int code) {
        this.httpCode = code;
        return this;
    }

    public Response setResponseText(String responseText) {
        this.responseText = responseText;
        return this;
    }

    public void output() throws IOException {
        System.out.println(socket);
        htmlResponse.outPutMessageWriter(socket, httpCode, responseText);

    }

}
