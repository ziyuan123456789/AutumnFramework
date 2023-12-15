package org.example.FrameworkUtils.ResponseType;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Cookie.Cookie;
import org.example.FrameworkUtils.ResponseType.Views.View;
import org.example.FrameworkUtils.ResponseWriter.HtmlResponse;

import java.io.IOException;
import java.net.Socket;

/**
 * @author ziyuan
 * @since 2023.12
 */
@Slf4j
public class Response {
    private final Socket socket;
    private Cookie cookie;
    private HtmlResponse htmlResponse;
    private int httpCode=200;
    private String responseText="";
    private View view;

    public Response(HtmlResponse htmlResponse, Socket socket) {
        this.htmlResponse = htmlResponse;
        this.socket = socket;

    }
    public Response setView(View view) {
        this.view = view;
        return this;
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

    public void outputMessage()  {
        try{
            htmlResponse.outPutMessageWriter(socket, httpCode, responseText,cookie);
        }catch (IOException e){
            log.error("htmlResponse输出失败");
        }
    }
    public void outputHtml()  {
        try{
            htmlResponse.outPutHtmlWriter(socket,view.getHtmlName(),cookie);
        }catch (IOException e){
            log.error("htmlResponse输出失败");
        }
    }

}
