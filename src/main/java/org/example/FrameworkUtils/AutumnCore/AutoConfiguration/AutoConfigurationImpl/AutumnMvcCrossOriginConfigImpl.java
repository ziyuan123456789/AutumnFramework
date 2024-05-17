package org.example.FrameworkUtils.AutumnCore.AutoConfiguration.AutoConfigurationImpl;

import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;
import org.example.FrameworkUtils.AutumnCore.AutoConfiguration.AutumnMvcCrossOriginConfig;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseWriter.CrossOriginBean;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyConfig
@MyConditional(MatchClassByInterface.class)
public class AutumnMvcCrossOriginConfigImpl implements AutumnMvcCrossOriginConfig {


    @Value("crossOrigin")
    String crossOriginString;

    @Override
    @AutumnBean
    public CrossOriginBean setAllowCrossOrigin() {
        CrossOriginBean crossOrigin=new CrossOriginBean();
        crossOrigin.setOrigins(crossOriginString);
        return crossOrigin;
    }
}
