package org.example.FrameworkUtils.AutumnCore.Ioc;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;
import org.example.FrameworkUtils.Exception.NotFindException;

import java.net.URL;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyComponent
public class ResourceFinder {

    @Value("htmlHome")
    private String htmlHome;

    @Value("iconHome")
    private String iconHome;

    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    public String getHtmlLocation(String htmlName) {
        String path;
        URL resource = classLoader.getResource(htmlHome + "/" + htmlName);
        if (resource != null) {
            path = resource.getPath();
        } else {
            throw new NotFindException("找不到对应的网页");
        }
        return path;
    }

    public String getIconLocation(String iconName) {
        String path;
        URL resource = classLoader.getResource(iconHome + "/" + iconName);
        if (resource != null) {
            path = resource.getPath();
        } else {
            throw new NotFindException("找不到对应的Icon");
        }
        return path;
    }

    public String getJsLocation(String jsUrl) {
        return null;
    }
}
