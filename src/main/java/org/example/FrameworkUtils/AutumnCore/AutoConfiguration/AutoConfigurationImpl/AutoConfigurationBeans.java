package org.example.FrameworkUtils.AutumnCore.AutoConfiguration.AutoConfigurationImpl;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnBeanFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

/**
 * @author wsh
 */
@MyConfig
@Slf4j
public class AutoConfigurationBeans implements BeanFactoryAware {
    private AutumnBeanFactory myContext;

    @AutumnBean
    public Reflections getReflection() {
        return new Reflections((String) myContext.get("packageUrl"), new SubTypesScanner(false));
    }

    @Override
    public void setBeanFactory(AutumnBeanFactory beanFactory) {
        this.myContext = beanFactory;
    }
}
