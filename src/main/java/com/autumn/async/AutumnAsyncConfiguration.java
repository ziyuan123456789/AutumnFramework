package com.autumn.async;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPreDestroy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author ziyuan
 * @since 2024.08
 */


@MyConfig
@Slf4j
//自动装配引入这个类进行配置线程池
public class AutumnAsyncConfiguration {

    private ExecutorService executor;

    @AutumnBean
    public ExecutorService taskExecutor() {
        this.executor = Executors.newFixedThreadPool(20, new ThreadFactory() {
            private int threadId = 1;

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("Custom-Executor-" + threadId++);
                return thread;
            }
        });
        return this.executor;
    }

    @MyPreDestroy
    public void destroy() {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
            log.info("线程池已销毁");
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            log.error("线程池销毁过程中发生中断", e);
            executor.shutdownNow();
        }
    }
}
