package org.example.FrameworkUtils.AutumnMVC.MvcImpl;

import org.example.FrameworkUtils.Annotation.AutunmnBean;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Annotation.MyConditional;
import org.example.FrameworkUtils.Annotation.MyConfig;
import org.example.FrameworkUtils.Annotation.Value;
import org.example.FrameworkUtils.AutumnMVC.AutumnMvcCrossOriginConfig;
import org.example.FrameworkUtils.ResponseWriter.CrossOriginBean;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
@MyConfig
@MyConditional(AutumnMvcCrossOriginConfig.class)
public class AutumnMvcCrossOriginConfigImpl implements AutumnMvcCrossOriginConfig {
    CrossOriginBean crossOrigin=new CrossOriginBean();

    @Value("crossOrigin")
    String crossOriginString;

    @Override
    @AutunmnBean
    public CrossOriginBean setAllowCrossOrigin() {
        crossOrigin.setOrigins(crossOriginString);
        return crossOrigin;
    }
}
