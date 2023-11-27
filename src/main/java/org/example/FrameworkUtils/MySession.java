package org.example.FrameworkUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MySession {
    private Map<String, Object> sessions = new ConcurrentHashMap<>();

    public String createSession() {
        String sessionId = generateUniqueSessionId();
        sessions.put(sessionId, new HashMap<String, Object>());
        return sessionId;
    }

    public Map<String, Object> getSession(String sessionId) {
        return (Map<String, Object>) sessions.getOrDefault(sessionId, null);
    }

    private String generateUniqueSessionId() {
        // 实现一个生成唯一session ID的方法
        return UUID.randomUUID().toString();
    }

    // 在响应中设置session cookie
    public void setSessionCookie(OutputStream out, String sessionId) throws IOException {
        String responseHeader = "Set-Cookie: JSESSIONID=" + sessionId + "; Path=/; HttpOnly\r\n";
        out.write(responseHeader.getBytes(StandardCharsets.UTF_8));
    }

    // 在后续请求中检索session
    public Map<String, Object> retrieveSessionFromRequest(String requestHeaders) {
        // 解析请求头中的Cookie字段，提取JSESSIONID的值
        String sessionId = extractSessionIdFromCookie(requestHeaders);
        return getSession(sessionId);
    }

    private String extractSessionIdFromCookie(String headers) {
        // 实现从请求头中提取JSESSIONID的逻辑
        // ...
        return null;
    }
}
