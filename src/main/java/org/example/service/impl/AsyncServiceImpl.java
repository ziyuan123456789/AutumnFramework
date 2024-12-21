package org.example.service.impl;

import com.autumn.async.Async;
import com.autumn.ormstarter.transaction.annotation.AutumnTransactional;
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
    @AutumnTransactional
    @Override
    public void asyncTest() {
        log.info("方法调用");
        try{
            Thread.sleep(10000);
        }catch (Exception e){
            log.error("异常", e);
        }
        System.out.println("asyncTest");
    }
}
