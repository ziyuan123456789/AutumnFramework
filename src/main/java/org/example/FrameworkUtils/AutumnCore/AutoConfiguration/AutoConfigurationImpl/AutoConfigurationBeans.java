package org.example.FrameworkUtils.AutumnCore.AutoConfiguration.AutoConfigurationImpl;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyContext;
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


    @AutumnBean
    public Reflections getReflection() {
        return new Reflections(packageName,new SubTypesScanner(false));
    }

}
