package org.example.FrameworkUtils.AutumnMVC.AutoConfiguration.AutoConfigurationImpl;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnMVC.Condition;
import org.example.FrameworkUtils.Webutils.MyContext;
import org.reflections.Reflections;

import java.util.Set;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyComponent
@Slf4j
public class MatchClassByInterface implements Condition {

    @MyAutoWired
    private Reflections reflections;

    @Override
    public void init(){
        log.info(this.getClass().getSimpleName() + "条件处理器中的初始化方法被执行");
    }


    @Override
    public boolean matches(MyContext myContext, Class<?> clazz) {
        Set<Class<?>> subTypesOf = (Set<Class<?>>) reflections.getSubTypesOf(clazz);

        for (Class<?> implClass : subTypesOf) {
            if (clazz.equals(implClass)) {
                continue;
            }
            MyConditional myConditionalAnnotation = implClass.getAnnotation(MyConditional.class);
            if (myConditionalAnnotation == null) {
                return false;
            } else {
                Class<? extends Condition> conditionClass = myConditionalAnnotation.value();
                Condition condition = myContext.getBean(conditionClass);

                if (condition.matches(myContext, implClass)) {
                    return false;

                }
            }
        }

        return true;
    }
}
