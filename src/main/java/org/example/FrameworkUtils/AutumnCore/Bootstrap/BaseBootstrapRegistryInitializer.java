package org.example.FrameworkUtils.AutumnCore.Bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.example.controller.DataController;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * 你听说过SpringApplication的构造方法有几步吗?
 * 什么?没听说过?那很遗憾你的面试没通过,回去蹲厕所的几分钟记得看看Java之父的小视频
 */
@Slf4j
public class BaseBootstrapRegistryInitializer implements BootstrapRegistryInitializer {
    @Override
    public void initialize(BootstrapRegistry registry) {
        DataController test = new DataController();
        registry.register(DataController.class, InstanceSupplier.of(test));
    }
}
