package org.example.FrameworkUtils.WebFrameworkBaseUtils.BaseController;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;
import org.example.FrameworkUtils.AutumnCore.AutoConfiguration.AutumnMvcConfiguration;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Icon;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyController
public class AutumnBaseController implements BeanFactoryAware {

    @Value("baseHtml")
    String baseHtml;

    @MyAutoWired
    private AutumnMvcConfiguration autumnMvcConfiguration;
    private ApplicationContext beanFactory;

    @MyRequestMapping("/favicon.ico")
    public Icon getIcon() {
        return new Icon("minireact.ico");
    }

    @MyRequestMapping("/")
    public View getMainPage() {
        return autumnMvcConfiguration.getMainPage();
    }

    @MyRequestMapping("/404")
    public View notFoundPage() {
        return autumnMvcConfiguration.get404Page();
    }


    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.beanFactory = beanFactory;
    }
}
