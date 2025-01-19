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
        DataController test = new DataController();
        registry.register(DataController.class, InstanceSupplier.of(test));
    }
}
