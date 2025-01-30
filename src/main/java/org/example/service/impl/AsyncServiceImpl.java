package org.example.service.impl;

import com.autumn.async.Async;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyService;
import org.example.service.AsyncService;

/**
 * @author ziyuan
 * @since 2024.08
 */
@MyService
@Slf4j
public class AsyncServiceImpl implements AsyncService {

    @Async
    @Override
    public void asyncTest() {
        log.info("耗时方法调用开始");
        try{
            Thread.sleep(5000);
            log.info("耗时方法调用结束,花费了5000ms");
        }catch (Exception e){
            log.error("异常", e);
        }
    }
}
