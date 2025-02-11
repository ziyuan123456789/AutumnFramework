package org.example.FrameworkUtils.AutumnCore.Event.Listener;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Bootstrap.BootstrapContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.env.ConfigurableEnvironment;

/**
 * @author ziyuan
 * @since 2025.01
 */

@Slf4j
public class BaseAutumnApplicationRunListener implements AutumnApplicationRunListener {

    static {
        log.info("BaseAutumnApplicationRunListener加载");
    }

    @Override
    public void starting(BootstrapContext bootstrapContext, Class<?> mainApplicationClass) {
        log.info("BaseAutumnApplicationRunListener感知到现在处于starting状态");

    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        log.info("BaseAutumnApplicationRunListener感知到现在处于environmentPrepared状态");
        environment.getApplicationArguments().getVmProperties().forEach(log::info);
    }

    @Override
    public void contextPrepared(ApplicationContext context) {
        log.info("BaseAutumnApplicationRunListener感知到现在处于contextPrepared状态");

    }


    @Override
    public void contextLoaded(ApplicationContext context) {
        log.info("BaseAutumnApplicationRunListener感知到现在处于contextLoaded状态");

    }

    @Override
    public void finished(ApplicationContext application, Exception exception) {

        log.info("BaseAutumnApplicationRunListener感知到现在处于finished状态");
    }


}
