package org.example.FrameworkUtils.AutumnCore.Bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.example.controller.AutumnContextController;

/**
 * @author ziyuan
 * @since 2025.01
 */

@Slf4j
public class BaseBootstrapRegistryInitializer implements BootstrapRegistryInitializer {
    @Override
    public void initialize(BootstrapRegistry registry) {
        AutumnContextController test = new AutumnContextController();
        registry.register(AutumnContextController.class, InstanceSupplier.of(test));
    }
}
