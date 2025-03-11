package org.example.FrameworkUtils.AutumnCore.AutoConfiguration.AutoConfigurationImpl;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Ioc.EnvironmentAware;
import org.example.FrameworkUtils.AutumnCore.env.Environment;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

/**
 * @author wsh
 */


@MyConfig
@Slf4j
public class AutoConfigurationBeans implements EnvironmentAware {
    private Environment environment;

    @AutumnBean
    public Reflections getReflection() {
        return new Reflections(environment.getProperty("autumn.main.package"), new SubTypesScanner(false));
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
