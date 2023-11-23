package org.example.FrameworkUtils.AutumnMVC;

import org.example.FrameworkUtils.Annotation.MyConditional;
import org.example.FrameworkUtils.Annotation.MyService;
import org.example.FrameworkUtils.Annotation.Value;
import org.example.FrameworkUtils.ResponseType.Views.View;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
@MyService
@MyConditional(AutumnMvcConfiguration.class)
public class AutumnMvcConfigurationImpl implements AutumnMvcConfiguration{
    @Value("baseHtml")
    String baseHtml;
    @Override
    public View getMainPage() {
        return new View(baseHtml);
    }
}
