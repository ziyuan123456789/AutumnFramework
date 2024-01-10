package org.example.FrameworkUtils.AutumnMVC.AutoConfiguration.AutoConfigurationImpl;

import org.example.FrameworkUtils.AutumnMVC.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnMVC.Annotation.Value;
import org.example.FrameworkUtils.AutumnMVC.AutoConfiguration.AutumnMvcCrossOriginConfig;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseWriter.CrossOriginBean;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyConfig
@MyConditional(MatchClassByInterface.class)
public class AutumnMvcCrossOriginConfigImpl implements AutumnMvcCrossOriginConfig {
    CrossOriginBean crossOrigin=new CrossOriginBean();

    @Value("crossOrigin")
    String crossOriginString;

    @Override
    @AutumnBean
    public CrossOriginBean setAllowCrossOrigin() {
        crossOrigin.setOrigins(crossOriginString);
        return crossOrigin;
    }
}
