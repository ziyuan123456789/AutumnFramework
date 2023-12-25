package org.example.FrameworkUtils.AutumnMVC.Controller;

import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyController;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.AutumnMVC.Annotation.Value;
import org.example.FrameworkUtils.AutumnMVC.AutoConfiguration.AutumnMvcConfiguration;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Icon;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Request;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyController
public class AutumnBaseController {

    @Value("baseHtml")
    String baseHtml;

    @MyAutoWired
    AutumnMvcConfiguration autumnMvcConfiguration;
    @MyRequestMapping("/favicon.ico")
    public Icon getIcon(Request request) {
        return new Icon("myicon.ico");
    }

    @MyRequestMapping("/")
    public View getMainPage(Request request) {
        return autumnMvcConfiguration.getMainPage();
    }

    @MyRequestMapping("/404")
    public View notFoundPage(Request request) {
        return autumnMvcConfiguration.get404Page();
    }


}
