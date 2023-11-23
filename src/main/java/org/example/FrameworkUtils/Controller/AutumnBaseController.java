package org.example.FrameworkUtils.Controller;

import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyController;
import org.example.FrameworkUtils.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.Annotation.Value;
import org.example.FrameworkUtils.AutumnMVC.AutumnMvcConfiguration;
import org.example.FrameworkUtils.ResponseType.Icon;
import org.example.FrameworkUtils.ResponseType.Views.View;
import org.example.FrameworkUtils.Webutils.Request;

/**
 * @author wangzhiyi
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


}
