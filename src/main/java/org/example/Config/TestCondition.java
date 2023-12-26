package org.example.Config;

import org.example.FrameworkUtils.AutumnMVC.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnMVC.MyCondition;
import org.example.FrameworkUtils.AutumnMVC.MyContext;

/**
 * @author ziyuan
 * @since 2023.12
 */
@MyComponent
public class TestCondition implements MyCondition {
    @Override
    public boolean matches(MyContext myContext, Class<?> clazz) {
        return false;
    }
}
