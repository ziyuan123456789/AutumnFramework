package org.example.FrameworkUtils.Session;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Orm.MyRedis.MyReidsTemplate;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @author wsh
 */
@Data
@Slf4j
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
    public void exitSave(){
        myReidsTemplate.init();
        myReidsTemplate.set("session",String.valueOf(sessions));
        log.info("Session序列化到Redis成功");
    }

}