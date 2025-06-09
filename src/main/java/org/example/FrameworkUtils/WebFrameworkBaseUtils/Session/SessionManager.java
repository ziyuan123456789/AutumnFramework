package org.example.FrameworkUtils.WebFrameworkBaseUtils.Session;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.Orm.MyRedis.MyRedisTemplate;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @author wsh
 */
@Data
@Slf4j
@MyComponent
public class SessionManager implements Serializable {
    private ConcurrentHashMap<String, MySession> sessions = new ConcurrentHashMap<>();
    @MyAutoWired
    MyRedisTemplate myRedisTemplate;

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
        try{
            myRedisTemplate.init();
            myRedisTemplate.set("session", String.valueOf(sessions));
            log.info("Session序列化到Redis成功");
        }catch (JedisConnectionException e){
            log.error("Session序列化到Redis失败,检查端口和ip是否正确?",e);
        }

    }

}
