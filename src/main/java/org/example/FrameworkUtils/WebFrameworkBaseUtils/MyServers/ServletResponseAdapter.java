package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Cookie.Cookie;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseWriter.TomCatHtmlResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author ziyuan
 * @since 2024.05
 */
@Slf4j
@Data
public class ServletResponseAdapter implements AutumnResponse {
    private final TomCatHtmlResponse tomCatHtmlResponse;
    private final HttpServletResponse response;
    private int httpCode = HttpServletResponse.SC_OK;
    private String responseText = "";
    private View view;

    public ServletResponseAdapter(HttpServletResponse response, TomCatHtmlResponse tomCatHtmlResponse) {
        this.response = response;
        this.tomCatHtmlResponse = tomCatHtmlResponse;

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
    public void outputErrorMessage(String title, String text, int code, List<String> origins) {
        try {
            String errorTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            tomCatHtmlResponse.outPutErrorMessageWriter(response, title, code, text, errorTime, null, origins);
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

    public void outputJavaScriptFile(String filePath) {
        try {
            // 设置返回内容的类型为 JavaScript
            response.setContentType("application/javascript");
            response.setStatus(httpCode);

            // 读取资源文件夹中的文件
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);

            if (inputStream == null) {
                log.error("JavaScript file not found: {}", filePath);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "JavaScript file not found");
                return;
            }

            // 将文件内容写入响应
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 PrintWriter writer = response.getWriter()) {

                String line;
                while ((line = reader.readLine()) != null) {
                    writer.println(line);
                }
                writer.flush();
            }

        } catch (IOException e) {
            log.error("Error in outputJavaScriptFile", e);
        }
    }

}
