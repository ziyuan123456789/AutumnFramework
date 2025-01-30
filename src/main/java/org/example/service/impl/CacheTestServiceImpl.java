package org.example.service.impl;

import com.autumn.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyService;
import org.example.service.CacheTestService;

/**
 * @author ziyuan
 * @since 2024.09
 */
@MyService
@Slf4j
public class CacheTestServiceImpl implements CacheTestService {

    @Cache
    @Override
    public String cacheTest(String name) {
        log.error("缓存失效");
        return "Hello " + name;
    }

}
