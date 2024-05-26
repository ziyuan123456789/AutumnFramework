package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyContext;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Cookie.Cookie;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseWriter.TomCatHtmlResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ziyuan
 * @since 2024.05
 */
@Slf4j
public class ServletResponseAdapter implements AutumnResponse {
    private  TomCatHtmlResponse tomCatHtmlResponse= (TomCatHtmlResponse) MyContext.getInstance().getBean(TomCatHtmlResponse.class.getName());
    private final HttpServletResponse response;
    private int httpCode = HttpServletResponse.SC_OK;
    private String responseText = "";
    private View view;

    public ServletResponseAdapter(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public AutumnResponse setView(View view) {
        this.view = view;
        return this;
    }

    @Override
    public AutumnResponse setCookie(Cookie cookie) {
        javax.servlet.http.Cookie servletCookie = new javax.servlet.http.Cookie(cookie.getCookieName(), cookie.getCookieValue());
        servletCookie.setPath(cookie.getPath());
        servletCookie.setMaxAge(cookie.getMaxAge());
        response.addCookie(servletCookie);
        return this;
    }

    @Override
    public AutumnResponse setCode(int code) {
        this.httpCode = code;
        return this;
    }

    @Override
    public AutumnResponse setResponseText(String responseText) {
        this.responseText = responseText;
        return this;
    }

    @Override
    public void outputMessage() {
        try {
            tomCatHtmlResponse.outPutMessageWriter(response, httpCode, responseText, null);
        } catch (IOException e) {
            log.error("Error in outputMessage", e);
        }
    }

    @Override
    public void outputErrorMessage() {
        try {
            String errorTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            tomCatHtmlResponse.outPutErrorMessageWriter(response, httpCode, responseText, errorTime, null);
        } catch (IOException e) {
            log.error("Error in outputErrorMessage", e);
        }
    }

    @Override
    public void outputHtml() {
        try {
            tomCatHtmlResponse.outPutHtmlWriter(response, view.getHtmlName(), null);
        } catch (IOException e) {
            log.error("Error in outputHtml", e);
        }
    }
}
