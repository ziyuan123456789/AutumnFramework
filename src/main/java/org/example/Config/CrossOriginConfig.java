package org.example.Config;

import org.example.Bean.Temp;
import org.example.FrameworkUtils.Annotation.AutunmnBean;
import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Annotation.MyConfig;
import org.example.FrameworkUtils.Annotation.MyService;
import org.example.FrameworkUtils.Annotation.Value;
import org.example.FrameworkUtils.AutumnMVC.AutoConfiguration.AutumnMvcCrossOriginConfig;
import org.example.FrameworkUtils.ResponseWriter.CrossOriginBean;
import org.example.mapper.TestMapper;

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
        crossOrigin.setOrigins("None");
        return crossOrigin;
    }
}
