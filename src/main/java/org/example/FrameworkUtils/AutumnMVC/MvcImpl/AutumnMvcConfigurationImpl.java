package org.example.FrameworkUtils.AutumnMVC.MvcImpl;

import org.example.FrameworkUtils.Annotation.MyConditional;
import org.example.FrameworkUtils.Annotation.MyService;
import org.example.FrameworkUtils.Annotation.Value;
import org.example.FrameworkUtils.AutumnMVC.AutumnMvcConfiguration;
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

    @Value("404Html")
    String notFoundPage;
    @Override
    public View getMainPage() {
        return new View(baseHtml);
    }

    @Override
    public View get404Page() {
        return new View(notFoundPage);
    }
}
