package org.example.FrameworkUtils.AutumnCore.AutoConfiguration.AutoConfigurationImpl;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

/**
 * @author wsh
 */

/**
 * 这也配叫自动装配??
 */
@MyConfig
@Slf4j
public class AutoConfigurationBeans implements BeanFactoryAware {
    private ApplicationContext myContext;

    @AutumnBean
    public Reflections getReflection() {
        return new Reflections((String) myContext.get("packageUrl"), new SubTypesScanner(false));
    }

    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.myContext = beanFactory;
    }
}
