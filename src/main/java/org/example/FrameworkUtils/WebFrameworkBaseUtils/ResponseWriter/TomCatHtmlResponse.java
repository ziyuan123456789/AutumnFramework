package org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseWriter;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Ioc.ResourceFinder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author ziyuan
 * @since 2024.05
 */
@MyComponent
@Slf4j
public class TomCatHtmlResponse {

    @MyAutoWired
    private ResourceFinder resourceFinder;


    //直接返回拼接的html文本
    public void outPutMessageWriter(HttpServletResponse response, int statusCode, String responseText, javax.servlet.http.Cookie cookie) throws IOException {
        outPutMessageWriter(response, statusCode, responseText, cookie, null);
    }


    public void outPutMessageWriter(HttpServletResponse response, int statusCode, String responseText, javax.servlet.http.Cookie cookie, List<String> crossOrigin) throws IOException {
        response.setStatus(statusCode);

        if (crossOrigin != null && !crossOrigin.isEmpty()) {
            response.setHeader("Access-Control-Allow-Origin", crossOrigin.stream().reduce((first, second) -> first + ", " + second)
                    .orElse("*"));
        }
        response.setContentType("text/html;charset=UTF-8");
        if (cookie != null) {
            response.addCookie(cookie);
        }
        response.getWriter().write(responseText);
    }

    //输出错误消息

    public void outPutMessageWriter(HttpServletResponse response, String title, int statusCode, String errorMessage, String errorTime, javax.servlet.http.Cookie cookie) throws IOException {
        outPutErrorMessageWriter(response, title, statusCode, errorMessage, errorTime, cookie, null);
    }

    public void outPutErrorMessageWriter(HttpServletResponse response, String title, int statusCode, String errorMessage, String errorTime, javax.servlet.http.Cookie cookie, List<String> crossOrigin) throws IOException {
        response.setStatus(statusCode);
        if (crossOrigin != null && !crossOrigin.isEmpty()) {
            response.setHeader("Access-Control-Allow-Origin", crossOrigin.stream().reduce((first, second) -> first + ", " + second)
                    .orElse("*"));
        }
        response.setContentType("text/html;charset=UTF-8");
        if (cookie != null) {
            response.addCookie(cookie);
        }
        if (title == null || title.isEmpty()) {
            title = "<p>服务器内部错误</p>";
        }
        String responseHtml = "<html><body>" +
                "<h1>" + statusCode + " Page</h1>" +
                title +
                "<p id='created'>" + errorTime + "</p>" +
                "<p id='created' style='color:red'>报错原因: " + errorMessage + "</p>" +
                "</body></html>";
        PrintWriter out = response.getWriter();
        out.write(responseHtml);
        out.close();
    }

    //302重定向
    public void redirectLocationWriter(HttpServletResponse response, String location) throws IOException {
        response.sendRedirect(location);
    }

    //返回HTML文件
    public void outPutHtmlWriter(HttpServletResponse response, String htmlFileName, javax.servlet.http.Cookie cookie) throws IOException {
        outPutHtmlWriter(response, htmlFileName, cookie, null);
    }

    public void outPutHtmlWriter(HttpServletResponse response, String htmlFileName, javax.servlet.http.Cookie cookie, List<String> crossOrigin) throws IOException {
        String filePath = resourceFinder.getHtmlLocation(htmlFileName).replaceFirst("^/", "");
        Path path = Path.of(filePath);
        if (crossOrigin != null && !crossOrigin.isEmpty()) {
            response.setHeader("Access-Control-Allow-Origin", crossOrigin.stream().reduce((first, second) -> first + ", " + second)
                    .orElse("*"));
        }
        byte[] responseBytes = Files.readAllBytes(path);
        response.setContentType("text/html;charset=UTF-8");
        response.setContentLength(responseBytes.length);
        if (cookie != null) {
            response.addCookie(cookie);
        }
        response.getOutputStream().write(responseBytes);
    }

    //返回icon
    public void outPutIconWriter(HttpServletResponse response, String iconFileName, javax.servlet.http.Cookie cookie) throws IOException {
        outPutIconWriter(response, iconFileName, cookie, null);
    }

    public void outPutIconWriter(HttpServletResponse response, String iconFileName, javax.servlet.http.Cookie cookie, List<String> crossOrigin) throws IOException {
        String filePath = resourceFinder.getIconLocation(iconFileName).replaceFirst("^/", "");
        Path path = Path.of(filePath);
        if (crossOrigin != null && !crossOrigin.isEmpty()) {
            response.setHeader("Access-Control-Allow-Origin", crossOrigin.stream().reduce((first, second) -> first + ", " + second)
                    .orElse("*"));
        }
        byte[] responseBytes = Files.readAllBytes(path);

        response.setContentType("image/x-icon");
        response.setContentLength(responseBytes.length);

        if (cookie != null) {
            response.addCookie(cookie);
        }

        response.getOutputStream().write(responseBytes);
    }

    //返回JavaScript文件
    public void outPutJavaScriptWriter(HttpServletResponse response, String jsFileName) throws IOException {
        outPutJavaScriptWriter(response, jsFileName, null);
    }

    public void outPutJavaScriptWriter(HttpServletResponse response, String jsFileName, List<String> crossOrigin) throws IOException {
        String filePath = resourceFinder.getHtmlLocation(jsFileName).replaceFirst("^/", "");
        Path path = Path.of(filePath);
        if (crossOrigin != null && !crossOrigin.isEmpty()) {
            response.setHeader("Access-Control-Allow-Origin", crossOrigin.stream().reduce((first, second) -> first + ", " + second)
                    .orElse("*"));
        }
        String htmlContent = Files.readString(path, StandardCharsets.UTF_8);
        byte[] responseBytes = htmlContent.getBytes(StandardCharsets.UTF_8);
        response.setContentType("application/javascript;charset=UTF-8");
        response.setContentLength(responseBytes.length);
        response.getOutputStream().write(responseBytes);
    }

}
