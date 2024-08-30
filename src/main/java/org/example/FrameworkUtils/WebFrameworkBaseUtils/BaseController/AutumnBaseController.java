package org.example.FrameworkUtils.WebFrameworkBaseUtils.BaseController;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;
import org.example.FrameworkUtils.AutumnCore.AutoConfiguration.AutumnMvcConfiguration;
import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnBeanFactory;
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
    private AutumnBeanFactory beanFactory;

    @MyRequestMapping("/favicon.ico")
    public Icon getIcon() {
        return new Icon("myicon.ico");
    }

    @MyRequestMapping("/")
    public View getMainPage() {
        System.out.println("woao");
        return autumnMvcConfiguration.getMainPage();
    }

    @MyRequestMapping("/404")
    public View notFoundPage() {
        return autumnMvcConfiguration.get404Page();
    }


    @Override
    public void setBeanFactory(AutumnBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
