package org.example.FrameworkUtils.AutumnCore.Bootstrap;

import org.example.controller.DataController;

/**
 * @author ziyuan
 * @since 2025.01
 */
public class BaseBootstrapRegistryInitializer implements BootstrapRegistryInitializer {
    @Override
    public void initialize(BootstrapRegistry registry) {
        DefaultAutumnBootstrapContext context = new DefaultAutumnBootstrapContext();
        DataController test = new DataController();
        context.register(DataController.class, test);
        registry.register(DataController.class, InstanceSupplier.of(test));
    }
}
