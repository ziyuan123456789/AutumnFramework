package com.autumn.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author ziyuan
 * @since 2024.09
 */
@Slf4j
public class CacheManager {
    private Map<String, Object> cacheStore = new WeakHashMap<>();

    public Boolean updateCache(String key, Object newValue) {
        if (cacheStore.containsKey(key)) {
            cacheStore.put(key, newValue);
            return true;
        }
        return false;
    }


    public Boolean removeAllCache() {
        cacheStore.clear();
        return true;
    }


    public Boolean removeCache(String key) {
        if (cacheStore.containsKey(key)) {
            cacheStore.remove(key);
            return true;
        }
        return false;
    }

    public Boolean addCache(String key, Object value) {
        if (!cacheStore.containsKey(key)) {
            cacheStore.put(key, value);
            return true;
        }
        return false;
    }

    public Object getCache(String key) {
        return cacheStore.get(key);
    }

    public Boolean containsCache(String key) {
        return cacheStore.containsKey(key);
    }
}
