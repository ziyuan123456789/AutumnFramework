package org.example.FrameworkUtils.ResponseWriter;

import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Cookie.Cookie;
import org.example.FrameworkUtils.Webutils.ResourceFinder;


import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyComponent
public class HtmlResponse {
    @MyAutoWired
    ResourceFinder resourceFinder;
    @MyAutoWired
    CrossOriginBean crossOriginBean;




    //xxx:http返回报文(直接返回拼接的html文本,Content-Type: text/html)
    public void outPutMessageWriter(Socket socket, int statusCode, String responseText,Cookie cookie) throws IOException {
        String  CrossOrigin=crossOriginBean.getOrigins();
        String responseTextWithHtml = "<html><body>" + "<h3 style='color:red'>" + responseText + "</h3>" + "</body></html>";
        byte[] responseBytes = responseTextWithHtml.getBytes(StandardCharsets.UTF_8);

        StringBuilder responseHeader = new StringBuilder();
        responseHeader.append("HTTP/1.1 ").append(statusCode).append(" OK\r\n");
        responseHeader.append("Server: liTangDingZhen\r\n");
        responseHeader.append("Content-Type: text/html;charset=UTF-8\r\n");
        responseHeader.append("Content-Length: ").append(responseBytes.length).append("\r\n");
        responseHeader.append("Connection: close\r\n");
        responseHeader.append("Access-Control-Allow-Origin: ").append(CrossOrigin).append("\r\n");
        if (cookie != null) {
            responseHeader.append("Set-Cookie: ")
                    .append(cookie.getCookieName()).append("=")
                    .append(cookie.getCookieValue()).append(";");
            if (cookie.getPath() != null) {
                responseHeader.append(" Path=").append(cookie.getPath()).append(";");
            }

            if (cookie.getMaxAge() > 0) {
                responseHeader.append(" Max-Age=").append(cookie.getMaxAge()).append(";");
            }
            responseHeader.append("\r\n");
        }
        responseHeader.append("\r\n");
        try (OutputStream out = socket.getOutputStream()) {
            out.write(responseHeader.toString().getBytes(StandardCharsets.UTF_8));
            out.write(responseBytes);
        }
    }

    //xxx:302重定向
    public void redirectLocationWriter(Socket socket, String location) throws IOException {
        String responseBody = "<html><body><h1>页面重定向/拦截器拦截</h1></body></html>";
        byte[] responseBodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        StringBuilder responseHeader = new StringBuilder();
        responseHeader.append("HTTP/1.1 302 Found\r\n");
        responseHeader.append("Location: ").append(location).append("\r\n");
        responseHeader.append("Content-Type: text/html; charset=UTF-8\r\n");
        responseHeader.append("Content-Length: ").append(responseBodyBytes.length).append("\r\n");
        responseHeader.append("Connection: close\r\n");
        responseHeader.append("\r\n");
        try (OutputStream out = socket.getOutputStream()) {
            out.write(responseHeader.toString().getBytes(StandardCharsets.UTF_8));
            out.write(responseBodyBytes);
        }
    }

    //xxx:http返回报文(返回找到的html文件,Content-Type: text/html)
    public void outPutHtmlWriter(Socket socket, String htmlUrl, Cookie cookie) throws IOException {
        String CrossOrigin = crossOriginBean.getOrigins();
        String filePath = resourceFinder.getHtmlLocation(htmlUrl).replaceFirst("^/", "");
        Path path = Path.of(filePath);
        byte[] responseBytes = Files.readAllBytes(path);
        StringBuilder responseHeader = new StringBuilder();
        responseHeader.append("HTTP/1.1 ").append(200).append(" OK\r\n");
        responseHeader.append("Server: liTangDingZhen\r\n");
        responseHeader.append("Content-Type: text/html;charset=UTF-8\r\n");
        responseHeader.append("Content-Length: ").append(responseBytes.length).append("\r\n");
        responseHeader.append("Connection: close\r\n");
        responseHeader.append("Access-Control-Allow-Origin: ").append(CrossOrigin).append("\r\n");
        if (cookie != null) {
            responseHeader.append("Set-Cookie: ")
                    .append(cookie.getCookieName()).append("=")
                    .append(cookie.getCookieValue()).append(";");
            if (cookie.getPath() != null) {
                responseHeader.append(" Path=").append(cookie.getPath()).append(";");
            }

            if (cookie.getMaxAge() > 0) {
                responseHeader.append(" Max-Age=").append(cookie.getMaxAge()).append(";");
            }
            responseHeader.append("\r\n");
        }

        responseHeader.append("\r\n");
        OutputStream out = socket.getOutputStream();
        out.write(responseHeader.toString().getBytes(StandardCharsets.UTF_8));
        out.write(responseBytes);
    }


    //xxx:http返回报文(返回找到的iocn,Content-Type: image/x-icon)
    public void outPutIconWriter(Socket socket, String htmlUrl,Cookie cookie) throws IOException {
        String filePath = resourceFinder.getIconLocation(htmlUrl).replaceFirst("^/", "");
        Path path = Path.of(filePath);
        byte[] responseBytes = Files.readAllBytes(path);
        StringBuilder responseHeader = new StringBuilder();
        responseHeader.append("HTTP/1.1 ").append(200).append(" OK\r\n");
        responseHeader.append("Server: liTangDingZhen\r\n");
        responseHeader.append("Content-Type: image/x-icon\r\n");
        responseHeader.append("Content-Length: ").append(responseBytes.length).append("\r\n");
        responseHeader.append("Connection: close\r\n");
        responseHeader.append("Access-Control-Allow-Origin: *\r\n");
        if (cookie != null) {
            responseHeader.append("Set-Cookie: ")
                    .append(cookie.getCookieName()).append("=")
                    .append(cookie.getCookieValue()).append(";");
            if (cookie.getPath() != null) {
                responseHeader.append(" Path=").append(cookie.getPath()).append(";");
            }

            if (cookie.getMaxAge() > 0) {
                responseHeader.append(" Max-Age=").append(cookie.getMaxAge()).append(";");
            }
            responseHeader.append("\r\n");
        }
        responseHeader.append("\r\n");
        try (OutputStream out = socket.getOutputStream()) {
            out.write(responseHeader.toString().getBytes(StandardCharsets.UTF_8));
            out.write(responseBytes);
        }
    }
    // xxx: http返回报文(返回JavaScript文件，Content-Type: application/javascript)
    public void outPutJavaScriptWriter(Socket socket, String jsUrl) throws IOException {
        String filePath = resourceFinder.getJsLocation(jsUrl).replaceFirst("^/", "");
        Path path = Path.of(filePath);
        byte[] responseBytes = Files.readAllBytes(path);
        StringBuilder responseHeader = new StringBuilder();
        responseHeader.append("HTTP/1.1 ").append(200).append(" OK\r\n");
        responseHeader.append("Server: YourServerName\r\n");
        responseHeader.append("Content-Type: application/javascript\r\n");
        responseHeader.append("Content-Length: ").append(responseBytes.length).append("\r\n");
        responseHeader.append("Connection: close\r\n");
        responseHeader.append("Access-Control-Allow-Origin: *\r\n");
        responseHeader.append("\r\n");

        try (OutputStream out = socket.getOutputStream()) {
            out.write(responseHeader.toString().getBytes(StandardCharsets.UTF_8));
            out.write(responseBytes);
        }
    }



}
