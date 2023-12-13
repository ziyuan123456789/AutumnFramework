package org.example.FrameworkUtils.Session;

import lombok.Data;
import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Orm.MyRedis.MyReidsTemplate;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @author wsh
 */
@Data
@MyComponent
public class SessionManager {
    private ConcurrentHashMap<String, MySession> sessions = new ConcurrentHashMap<>();
    @MyAutoWired
    MyReidsTemplate myReidsTemplate;

    public MySession getSession(String sessionId) {
        if (sessionId != null && sessions.containsKey(sessionId)) {
            return sessions.get(sessionId);
        } else {
            MySession newSession = new MySession(sessionId);
            sessions.put(newSession.getSessionId(), newSession);
            return newSession;
        }
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }
    private void close(){

    }
}