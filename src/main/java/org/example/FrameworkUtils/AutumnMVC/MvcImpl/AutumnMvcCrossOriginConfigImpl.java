package org.example.FrameworkUtils.AutumnMVC.MvcImpl;

import org.example.FrameworkUtils.Annotation.AutunmnBean;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnMVC.AutumnMvcCrossOriginConfig;
import org.example.FrameworkUtils.ResponseWriter.CrossOriginBean;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
@MyComponent
@MyConditional(AutumnMvcCrossOriginConfig.class)
public class AutumnMvcCrossOriginConfigImpl implements AutumnMvcCrossOriginConfig {
    CrossOriginBean crossOrigin1=new CrossOriginBean();



    @Override
    @AutunmnBean
    public CrossOriginBean setAllowCrossOrigin() {
        String[] defaultOrigins=new String[1];
        defaultOrigins[0]="None";
        crossOrigin1.setOrigins(new String[0]);
        return crossOrigin1;
    }
}
