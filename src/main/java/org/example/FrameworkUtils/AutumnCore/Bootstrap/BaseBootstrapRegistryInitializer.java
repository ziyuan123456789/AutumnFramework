package org.example.FrameworkUtils.AutumnCore.Bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Utils.BeanUtils;

/**
 * @author ziyuan
 * @since 2025.01
 */

@Slf4j
public class BaseBootstrapRegistryInitializer implements BootstrapRegistryInitializer {
    static {
        log.info("BaseBootstrapRegistryInitializer加载");
    }


    @Override
    public void initialize(BootstrapRegistry registry) {
        BeanUtils utils = new BeanUtils();
        registry.register(BeanUtils.class, InstanceSupplier.of(utils));
    }
}
