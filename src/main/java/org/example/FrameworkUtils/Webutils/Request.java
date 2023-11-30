package org.example.FrameworkUtils.Webutils;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.MyMultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2023.10
 */

@SuppressWarnings("all")
@Slf4j
public class Request {
    private String payload;
    private String body;
    private String method;
    private String url;

    private Integer contentLength;
    private MyMultipartFile myMultipartFile;
    private String contentType;
    private String boundary;


    public Request(String payload, String body, Integer contentLength) {
        this.payload = payload;
        this.body = body;
//        System.out.println(body);
        parseRequest(payload);
        if (getMethod().equals("GET")) {
            this.contentLength = null;
        } else {
            this.contentLength = contentLength;
        }
    }
    //xxx:文件上传专用
    public Request(String payload, String body, Integer contentLength, String contentType, String boundary) {
        this.payload = payload;
        this.body = body;
        System.out.println(body);
        parseRequest(payload);
        this.contentType = contentType;
        this.boundary = boundary;
        if (getMethod().equals("GET")) {
            this.contentLength = null;
        } else {
            this.contentLength = contentLength;
        }
    }

    private void parseRequest(String payload) {
        try {
            String firstLine = payload.split("\n")[0];
            String[] parts = firstLine.split(" ");
            this.method = parts[0];
            this.url = parts.length > 1 ? parts[1] : "";
        } catch (Exception e) {
            log.error("Error parsing request: " + e.getMessage());
        }
    }


    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }


    public void setParameters(String url) {
        this.url = url;
    }


    public  Map<String, String> getParameters() {
        Map<String, String> queryParams = new HashMap<>();
        if (method.equals("GET")) {
            int questionMarkIndex = url.indexOf('?');
            if (questionMarkIndex != -1) {
                String queryString = url.substring(questionMarkIndex + 1);
                String[] paramPairs = queryString.split("&");
                for (String paramPair : paramPairs) {
                    String[] keyValue = paramPair.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = keyValue[1];
                        queryParams.put(key, value);
                    }
                }
            }
            return queryParams;
        } else {
            int contentLength = getContentLength(payload);
            String[] paramPairs = body.split("&");
            for (String paramPair : paramPairs) {
                String[] keyValue = paramPair.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];
                    queryParams.put(key, value);
                }
            }

            return queryParams;
        }

    }

    private int getContentLength(String request) {
        String[] lines = request.split("\n");
        for (String line : lines) {
            if (line.startsWith("Content-Length:")) {
                return Integer.parseInt(line.substring("Content-Length:".length()).trim());
            }
        }
        return 0;
    }

    public String getBody() {
        return body;
    }


    public Integer getContentLength() {
        return contentLength;
    }

    public MyMultipartFile getMyMultipartFile() {
        return myMultipartFile;
    }

    public void setMyMultipartFile(MyMultipartFile myMultipartFile) {
        this.myMultipartFile = myMultipartFile;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBoundary() {
        return boundary;
    }

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }



}








