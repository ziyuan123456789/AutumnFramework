package org.example.FrameworkUtils.AutumnCore.Bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.example.controller.DataController;

/**
 * @author ziyuan
 * @since 2025.01
 */
@Slf4j
public class BaseBootstrapRegistryInitializer implements BootstrapRegistryInitializer {
    @Override
    public void initialize(BootstrapRegistry registry) {
        log.info("BaseBootstrapRegistryInitializer加载");
        DefaultAutumnBootstrapContext context = new DefaultAutumnBootstrapContext();
        DataController test = new DataController();
        context.register(DataController.class, test);
        registry.register(DataController.class, InstanceSupplier.of(test));
    }
}
