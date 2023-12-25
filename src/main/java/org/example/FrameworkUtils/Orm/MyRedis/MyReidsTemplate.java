package org.example.FrameworkUtils.Orm.MyRedis;

import org.example.FrameworkUtils.AutumnMVC.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnMVC.Annotation.Value;
import redis.clients.jedis.Jedis;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyComponent
public class MyReidsTemplate {
    @Value("redisHost")
    String host;
    @Value("redisPort")
    Integer port;

    private Jedis jedis;

    public void init() {
        this.jedis = new Jedis(host, port);
    }
    public void close() {
        if (jedis != null) {
            jedis.close();
        }
    }

    public void set(String key, String value) {
        jedis.set(key, value);
    }

    public String get(String key) {
        return jedis.get(key);
    }

    public void delete(String key) {
        jedis.del(key);
    }

}
