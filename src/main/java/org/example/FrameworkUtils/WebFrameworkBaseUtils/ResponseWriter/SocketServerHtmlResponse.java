package org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseWriter;


import com.autumn.mvc.WebSocket.WebSocketEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.ResourceFinder;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Cookie.Cookie;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ziyuan
 * @since 2023.11
 */
@Slf4j
@MyComponent
@Deprecated
public class SocketServerHtmlResponse implements BeanFactoryAware {

    @MyAutoWired
    private ResourceFinder resourceFinder;

    @MyAutoWired
    private CrossOriginBean crossOriginBean;

    @MyAutoWired
    private AnnotationScanner annotationScanner;

    private ApplicationContext beanFactory;

    //xxx:http返回报文(直接返回拼接的html文本,Content-Type: text/html)
    public void outPutMessageWriter(Socket socket, int statusCode, String responseText,Cookie cookie) throws IOException {
        String  CrossOrigin=crossOriginBean.getOrigins();
        byte[] responseBytes = responseText.getBytes(StandardCharsets.UTF_8);

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

    public void outPutErrorMessageWriter(Socket socket, int statusCode, String errorMessage, String errorTime, Cookie cookie) throws IOException {
        String  CrossOrigin=crossOriginBean.getOrigins();
        String responseText = "<html><body>" +
                "<h1>"+statusCode+" Error Page</h1>" +
                "<p>服务器内部错误</p>" +
                "<p id='created'>" + errorTime + "</p>" +
                "<p>There was an unexpected error (type=Internal Server Error, status="+statusCode+").</p>" +
                "<p id='created' style='color:red'>报错原因:" + errorMessage + "</p>" +
                "</body></html>";
        byte[] responseBytes = responseText.getBytes(StandardCharsets.UTF_8);
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


    //xxx:http返回报文(返回找到的icon,Content-Type: image/x-icon)
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


    public void outPutSocketWriter(Socket clientSocket, String requestHeaders, String url) throws IOException {

        WebSocketEndpoint webSocketMaster = null;
        Pattern pattern = Pattern.compile("Sec-WebSocket-Key: (.+)");
        Matcher matcher = pattern.matcher(requestHeaders);
        if (!matcher.find()) {
            throw new RuntimeException("没有WebSocketKey");
        }
        String secWebSocketKey = matcher.group(1).trim();
        OutputStream out = clientSocket.getOutputStream();
        try {
            String MAGIC_STRING = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            secWebSocketKey += MAGIC_STRING;
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(secWebSocketKey.getBytes(StandardCharsets.UTF_8));
            String responseKey = Base64.getEncoder().encodeToString(hash);

            StringBuilder responseHeader = new StringBuilder();
            responseHeader.append("HTTP/1.1 101 Switching Protocols\r\n");
            responseHeader.append("Upgrade: WebSocket\r\n");
            responseHeader.append("Connection: Upgrade\r\n");
            responseHeader.append("Sec-WebSocket-Accept: ").append(responseKey).append("\r\n");
            responseHeader.append("\r\n");


            out.write(responseHeader.toString().getBytes(StandardCharsets.UTF_8));
//            for (Map.Entry<String, Object> entry : beanFactory.getIocContainer().entrySet()) {
//                try{
//                    Class<?> clazz = Class.forName(entry.getKey());
//                    if (clazz.isAnnotationPresent(MyWebSocketEndpoint.class)) {
//                        MyWebSocketEndpoint annotation = clazz.getAnnotation(MyWebSocketEndpoint.class);
//                        if (url.equals(annotation.value())) {
//                            webSocketMaster = (WebSocketBaseConfig) beanFactory.getBean(entry.getKey());
//                            break;
//                        }
//                    }
//                }catch (Exception e){
//
//                }
//
//            }
            if (webSocketMaster == null) {
                throw new RuntimeException("没有符合的WebSocket处理器");
            } else {
                webSocketMaster.onOpen();
            }


        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-1失败");
        }
        try {
            InputStream in = clientSocket.getInputStream();
            byte[] buffer = new byte[4096];
            int bytes;

            while ((bytes = in.read(buffer)) != -1) {
                if (bytes < 2) {
                    continue; // 最小的帧头长度是2字节，继续等待数据
                }

                boolean isFinalFragment = (buffer[0] & 0x80) != 0; // 检查FIN位
                int opcode = buffer[0] & 0x0F; // 获取操作码
                boolean isMasked = (buffer[1] & 0x80) != 0; // 检查MASK位
                int payloadLength = buffer[1] & 0x7F; // 初始负载长度

                if (opcode == 0x1 && isMasked && isFinalFragment) { // 文本帧、被掩码、是最后的片段
                    // 确定掩码键和负载数据的起始位置
                    int maskingKeyIndex = 2;
                    if (payloadLength == 126) {
                        maskingKeyIndex = 4;
                    } else if (payloadLength == 127) {
                        maskingKeyIndex = 10;
                    }
                    int payloadIndex = maskingKeyIndex + 4; // 负载数据紧随掩码键之后

                    // 应用掩码键
                    for (int i = 0; i < payloadLength; i++) {
                        buffer[payloadIndex + i] ^= buffer[maskingKeyIndex + (i % 4)];
                    }
                    String message = new String(buffer, payloadIndex, payloadLength, StandardCharsets.UTF_8);

                    out.write(encodeTextFrame(webSocketMaster.onMsg(message)));
                }
            }
        } catch (IOException e) {

            if (webSocketMaster != null) {
                webSocketMaster.onClose();
            }
        } finally {
            try {
                out.close();
                clientSocket.close();
            } catch (IOException ex) {
                log.error("关闭连接异常", ex);
            }
        }


    }


    public static byte[] encodeTextFrame(String message) {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        int messageLength = messageBytes.length;

        // 创建数据帧字节数组
        byte[] frame;

        // 根据消息长度确定数据帧的长度前缀
        if (messageLength <= 125) {
            frame = new byte[messageLength + 2];
            frame[1] = (byte) messageLength;
        } else if (messageLength <= 65535) {
            frame = new byte[messageLength + 4];
            frame[1] = (byte) 126;
            frame[2] = (byte) ((messageLength >> 8) & 0xFF);
            frame[3] = (byte) (messageLength & 0xFF);
        } else {
            frame = new byte[messageLength + 10];
            frame[1] = (byte) 127;
            frame[2] = (byte) ((messageLength >> 56) & 0xFF);
            frame[3] = (byte) ((messageLength >> 48) & 0xFF);
            frame[4] = (byte) ((messageLength >> 40) & 0xFF);
            frame[5] = (byte) ((messageLength >> 32) & 0xFF);
            frame[6] = (byte) ((messageLength >> 24) & 0xFF);
            frame[7] = (byte) ((messageLength >> 16) & 0xFF);
            frame[8] = (byte) ((messageLength >> 8) & 0xFF);
            frame[9] = (byte) (messageLength & 0xFF);
        }

        // 设置 FIN 位和操作码（0x81 表示文本帧）
        frame[0] = (byte) 0x81;

        // 复制消息数据到数据帧
        System.arraycopy(messageBytes, 0, frame, frame.length - messageLength, messageLength);

        return frame;
    }

    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.beanFactory = beanFactory;
    }
}
