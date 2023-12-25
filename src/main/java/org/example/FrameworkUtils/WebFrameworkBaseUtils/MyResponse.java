package org.example.FrameworkUtils.WebFrameworkBaseUtils;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Cookie.Cookie;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseWriter.HtmlResponse;

import java.io.IOException;
import java.net.Socket;

/**
 * @author ziyuan
 * @since 2023.12
 */
@Slf4j
public class MyResponse {
    private final Socket socket;
    private Cookie cookie;
    private HtmlResponse htmlResponse;
    private int httpCode=200;
    private String responseText="";
    private View view;

    public MyResponse(HtmlResponse htmlResponse, Socket socket) {
        this.htmlResponse = htmlResponse;
        this.socket = socket;

    }
    public MyResponse setView(View view) {
        this.view = view;
        return this;
    }

    public MyResponse setCookie(Cookie cookie) {
        this.cookie = cookie;
        return this;
    }

    public MyResponse setCode(int code) {
        this.httpCode = code;
        return this;
    }


    public MyResponse setResponseText(String responseText) {
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
