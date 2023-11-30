package org.example.FrameworkUtils.AutumnMVC.AutoConfiguration.AutoConfigurationImpl;

import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnMVC.Condition;
import org.example.FrameworkUtils.Webutils.MyContext;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
@MyComponent
public class MatchClassByInterface implements Condition {
    @Override
    public boolean matches(MyContext myContext, Class<?> clazz) {
        return false;
    }
}
