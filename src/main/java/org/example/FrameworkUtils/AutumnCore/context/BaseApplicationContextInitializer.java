package org.example.FrameworkUtils.AutumnCore.context;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.env.Environment;

import java.util.Arrays;

/**
 * @author ziyuan
 * @since 2025.01
 */
@Slf4j
public class BaseApplicationContextInitializer implements ApplicationContextInitializer {

    //利用这个扩展点,你可以远程拉取一些配置文件,或者进行一些初始化操作
    @Override
    public void initialize(ApplicationContext context) {
        log.info("BaseApplicationContextInitializer加载");
        Environment environment = context.getEnvironment();
        log.info("当前激活的配置文件: {}", Arrays.toString(environment.getActiveProfiles()));
    }
}
