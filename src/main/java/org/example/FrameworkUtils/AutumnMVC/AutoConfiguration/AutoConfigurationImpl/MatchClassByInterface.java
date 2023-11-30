package org.example.FrameworkUtils.AutumnMVC.AutoConfiguration.AutoConfigurationImpl;

import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnMVC.Condition;
import org.example.FrameworkUtils.Webutils.MyContext;
import org.reflections.Reflections;

import java.util.Set;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
@MyComponent
public class MatchClassByInterface implements Condition {

    @MyAutoWired
    private Reflections reflections;

    @Override
    public boolean matches(MyContext myContext, Class<?> clazz) {
        Set<Class<?>> subTypesOf = (Set<Class<?>>) reflections.getSubTypesOf(clazz);

        for (Class<?> implClass : subTypesOf) {
            if (this.getClass().equals(implClass)) {
                continue;
            }
            MyConditional myConditionalAnnotation = implClass.getAnnotation(MyConditional.class);
            if (myConditionalAnnotation == null) {
                return false;
            } else {
                Class<? extends Condition> conditionClass = myConditionalAnnotation.value();
                Condition condition = (Condition) myContext.getBean(conditionClass);

                if (condition.matches(myContext, implClass)) {
                    return false;
                }
            }
        }

        return true;
    }
}
