package org.example.controller;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.service.CacheTestService;

/**
 * @author ziyuan
 * @since 2025.06
 */
@MyController
@MyRequestMapping("/cache")
public class AutumnCacheController {


    @MyAutoWired
    private CacheTestService cacheTestService;


    //测试缓存组件
    @MyRequestMapping("/cache")
    public String cacheTest(String name) {
        return cacheTestService.cacheTest(name);

    }
}
