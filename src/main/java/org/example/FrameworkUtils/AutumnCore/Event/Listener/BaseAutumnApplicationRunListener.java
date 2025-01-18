package org.example.FrameworkUtils.AutumnCore.Event.Listener;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Bootstrap.BootstrapContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyContext;

/**
 * @author ziyuan
 * @since 2025.01
 */
@Slf4j
public class BaseAutumnApplicationRunListener implements AutumnApplicationRunListener {

    @Override
    public void starting(BootstrapContext bootstrapContext, Class<?> mainApplicationClass) {
        log.info("BaseAutumnApplicationRunListener加载");

    }

    @Override
    public void contextPrepared(MyContext context) {
        log.info("BaseAutumnApplicationRunListener加载contextPrepared");

    }


    @Override
    public void contextLoaded(MyContext context) {
        log.info("BaseAutumnApplicationRunListener加载contextLoaded");

    }

    @Override
    public void finished(MyContext application, Exception exception) {

        log.info("BaseAutumnApplicationRunListener加载finished");
    }


}
