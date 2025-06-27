package org.example.Config;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.AutoConfiguration.AutumnMvcConfiguration;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyConfig
public class MvcConfig implements AutumnMvcConfiguration {
    @Override
    public View getMainPage() {
        return new View("NewSwagger.html");
    }

    @Override
    public View get404Page() {
        return new View("404.html");
    }
}
