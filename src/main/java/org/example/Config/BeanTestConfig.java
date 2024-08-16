package org.example.Config;

import lombok.extern.slf4j.Slf4j;
import org.example.Bean.Car;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPostConstruct;
import org.example.mapper.UserMapper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author ziyuan
 * @since 2024.04
 */
@MyConfig
@Slf4j
public class BeanTestConfig {


    @MyAutoWired
    UserMapper userMapper;

    @MyPostConstruct
    public void init() {
        log.warn("现在我们来测试orm是否正常,但你很可能没有修改配置文件导致一段时间的阻塞,于是我限制了代码运行时间,如果三秒钟还没有结束那就是配置文件存在问题,我们就跳过这个环节继续运行");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            try {
                log.warn(userMapper.getAllUser(1).toString());
            } catch (Exception e) {
                log.error("SQL 执行失败了, 但没关系, 继续运行");
            }
        });

        try {
            future.get(3, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("方法执行时间超过5秒，抛出异常");
            future.cancel(true);
        } catch (InterruptedException | ExecutionException e) {
            log.error("执行过程中发生异常", e);
        } finally {
            executor.shutdown();
        }
    }
    @AutumnBean("BYD")
    public Car giveMeBydCar() throws Exception {
        Car car=new Car();
        car.setName("BYD");
        return car;
    }

    @AutumnBean("WenJie")
    public Car giveMeWenJieCar(){
        Car car=new Car();
        car.setName("WenJie");
        return car;
    }


}
