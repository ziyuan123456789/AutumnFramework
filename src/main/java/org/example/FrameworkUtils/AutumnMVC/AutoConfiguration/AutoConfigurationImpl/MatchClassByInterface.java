package org.example.FrameworkUtils.AutumnMVC.AutoConfiguration.AutoConfigurationImpl;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnMVC.MyCondition;
import org.example.FrameworkUtils.AutumnMVC.MyContext;
import org.reflections.Reflections;

import java.util.Set;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyComponent
@Slf4j
public class MatchClassByInterface implements MyCondition {

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
                Class<? extends MyCondition> conditionClass = myConditionalAnnotation.value();
                MyCondition myCondition = myContext.getBean(conditionClass);

                if (myCondition.matches(myContext, implClass)) {
                    return false;

                }
            }
        }

        return true;
    }
}
