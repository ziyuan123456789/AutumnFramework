package org.example.FrameworkUtils.AutumnCore.context;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyContext;

/**
 * @author ziyuan
 * @since 2025.01
 */
@Slf4j
public class BaseApplicationContextInitializer implements ApplicationContextInitializer {

    @Override
    public void initialize(ApplicationContext context) {
        log.info("BaseApplicationContextInitializer加载");
    }
}
