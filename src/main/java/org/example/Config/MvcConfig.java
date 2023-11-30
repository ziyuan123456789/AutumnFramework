package org.example.Config;

import org.example.FrameworkUtils.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnMVC.AutoConfiguration.AutumnMvcConfiguration;
import org.example.FrameworkUtils.ResponseType.Views.View;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
@MyConfig
public class MvcConfig implements AutumnMvcConfiguration {
    @Override
    public View getMainPage() {
        return new View("index.html");
    }

    @Override
    public View get404Page() {
        return new View("404.html");
    }
}
