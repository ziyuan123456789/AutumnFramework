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
        log.info("方法调用");
        try{
            Thread.sleep(10000);
            System.out.println("asyncTest");
        }catch (Exception e){

        }

    }
}
