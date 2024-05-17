package org.example.Config;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.AutoConfiguration.AutumnMvcConfiguration;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;

/**
 * @author ziyuan
 * @since 2023.12
 */
@MyConfig
@MyConditional(TestCondition.class)
public class Mvc2 implements AutumnMvcConfiguration {
    @Override
    public View getMainPage() {
        return new View("AutumnFrameworkMainPage.html");
    }

    @Override
    public View get404Page() {
        return new View("404.html");
    }
}