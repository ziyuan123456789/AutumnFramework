package org.example.Config;

import org.example.FrameworkUtils.Annotation.MyService;
import org.example.FrameworkUtils.AutumnMVC.AutumnMvcConfiguration;
import org.example.FrameworkUtils.ResponseType.Views.View;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
@MyService
public class PageInit implements AutumnMvcConfiguration {
    @Override
    public View getMainPage() {
        return new View("uploadfile.html");
    }
}
