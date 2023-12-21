package org.example.FrameworkUtils.Session;

import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ziyuan
 * @since 2023.12
 */
@Data
public class MySession {
    private String  sessionId;
    private ConcurrentHashMap<String,Object> indexMap=new ConcurrentHashMap<>();

    public MySession(String sessionId) {
        this.sessionId = sessionId;
    }
    public void setAttribute(String key,Object value){
        indexMap.put(key,value);
    }
    public Object getAttribute(String key){
        return indexMap.get(key);
    }
}
