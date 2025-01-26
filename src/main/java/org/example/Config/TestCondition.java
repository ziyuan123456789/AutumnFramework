package org.example.Config;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyCondition;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyContext;

/**
 * @author ziyuan
 * @since 2023.12
 */
@MyComponent
public class TestCondition implements MyCondition {
    @Override
    public boolean matches(ApplicationContext myContext, Class<?> clazz) {
        return false;
    }
}
