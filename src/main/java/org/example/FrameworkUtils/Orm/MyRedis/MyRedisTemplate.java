package org.example.FrameworkUtils.Orm.MyRedis;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;
import redis.clients.jedis.Jedis;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyComponent
public class MyRedisTemplate {

    @Value("redisHost")
    private String host;

    @Value("redisPort")
    private Integer port;

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
