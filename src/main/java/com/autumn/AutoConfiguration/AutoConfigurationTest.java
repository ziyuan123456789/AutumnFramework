package com.autumn.AutoConfiguration;

import lombok.extern.slf4j.Slf4j;
import org.example.Config.TestCondition;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;

/**
 *
 *
 * @author ziyuan
 * @since 2025.08
 */
@MyConfig
@Slf4j
@ConditionalOnClass("com.年终奖")
public class AutoConfigurationTest {

    @AutumnBean
    public Object testConditionOnClass() {
        return new TestCondition();
    }

}
