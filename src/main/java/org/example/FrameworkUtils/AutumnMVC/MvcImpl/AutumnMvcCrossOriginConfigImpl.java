package org.example.FrameworkUtils.AutumnMVC.MvcImpl;

import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Annotation.MyConditional;
import org.example.FrameworkUtils.Annotation.MyService;
import org.example.FrameworkUtils.Annotation.Value;
import org.example.FrameworkUtils.AutumnMVC.AutumnMvcConfiguration;
import org.example.FrameworkUtils.AutumnMVC.AutumnMvcCrossOriginConfig;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
@MyComponent
@MyConditional(AutumnMvcCrossOriginConfig.class)
public class AutumnMvcCrossOriginConfigImpl implements AutumnMvcCrossOriginConfig {
    String[] defaultOrigins=new String[0];
    @Override
    public String[] setAllowCrossOrigin(String[] origins) {
        return defaultOrigins;
    }
}
