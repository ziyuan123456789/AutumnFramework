package org.example.FrameworkUtils.AutumnMVC.AutoConfiguration.AutoConfigurationImpl;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.AutunmnBean;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnMVC.MyContext;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

/**
 * @author wsh
 */
@MyConfig
@Slf4j
public class AutoConfigurationBeans {
    private final MyContext myContext=MyContext.getInstance();
    String packageName = (String) myContext.get("packageUrl");

    @AutunmnBean
    @SuppressWarnings("deprecation")
    public Reflections getReflection() {
        return new Reflections(packageName,new SubTypesScanner(false));
    }

}
