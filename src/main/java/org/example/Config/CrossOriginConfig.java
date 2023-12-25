package org.example.Config;

import org.example.FrameworkUtils.AutumnMVC.Annotation.AutunmnBean;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnMVC.AutoConfiguration.AutumnMvcCrossOriginConfig;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseWriter.CrossOriginBean;

/**
 * @author ziyuan
 * @since 2023.10
 */
@MyConfig
public class CrossOriginConfig implements AutumnMvcCrossOriginConfig {

    CrossOriginBean crossOrigin=new CrossOriginBean();

    @Override
    @AutunmnBean
    public CrossOriginBean setAllowCrossOrigin() {
        crossOrigin.setOrigins("*");
        return crossOrigin;
    }
}
