package org.example.FrameworkUtils.AutumnCore.AutoConfiguration.AutoConfigurationImpl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyCondition;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author ziyuan
 * @since 2023.11
 */

/**
 * 混沌之初,有条件注解,有条件处理器,有条件处理器的实现类,"自动装配"也应该诞生了
 */
//@MyComponent
@Slf4j
@Data
public class MatchClassByInterface implements MyCondition {

    //    @MyAutoWired
    Reflections reflections = new Reflections("org.example", new SubTypesScanner(false));

    @Override
    public boolean matches(ApplicationContext myContext, Class<?> clazz) {
        Set<Class<?>> subTypesOf = (Set) reflections.getSubTypesOf(clazz.getInterfaces()[0]);
        List<Class> injectImplList = new ArrayList<>();

        if (subTypesOf.size() == 2) {
            return false;
        } else if (subTypesOf.size() > 2) {
            for (Class<?> implClass : subTypesOf) {
                if (implClass.equals(clazz)) {
                    continue;
                }

                MyConditional myCondition = implClass.getAnnotation(MyConditional.class);

                if (myCondition != null) {
                    Class<? extends MyCondition>[] otherConditions = myCondition.value();
                    boolean conditionMatched = false;

                    for (Class<? extends MyCondition> conditionClass : otherConditions) {
                        MyCondition myConditionImpl = (MyCondition) myContext.getBean(conditionClass);
                        if (myConditionImpl.matches(myContext, implClass)) {
                            conditionMatched = true;
                            break;
                        }
                    }

                    if (conditionMatched) {
                        throw new IllegalStateException("多个条件处理器均被命中, 请确认到底要注入哪一个: " + injectImplList);
                    }

                } else {
                    injectImplList.add(implClass);
                }
            }
        }
        if (injectImplList.size() == 1) {
            return false;
        } else if (injectImplList.size() > 1) {
            throw new IllegalStateException("多个条件处理器均被命中, 请确认到底要注入哪一个: " + injectImplList);
        }

        return true;
    }

}
