package org.example.FrameworkUtils.AutumnMVC.AutoConfiguration.AutoConfigurationImpl;

import javassist.tools.reflect.Reflection;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Annotation.AutunmnBean;
import org.example.FrameworkUtils.Annotation.MyConfig;
import org.example.FrameworkUtils.Webutils.MyContext;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

@MyConfig
@Slf4j
public class AutoConfigurationBeans {
    private MyContext myContext=MyContext.getInstance();
     String packageName = (String) myContext.get("packageUrl");

    @AutunmnBean
    @SuppressWarnings("deprecation")
    public Reflections getReflection() {
        log.warn("AutunmnBean初始化");
        return new Reflections(packageName,new SubTypesScanner(false));
    }

}
