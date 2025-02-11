package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.ConditionCheck;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyCondition;
@MyComponent
@Slf4j
public class SocketServerConditionCheck implements MyCondition {

    @Override
    public boolean matches(ApplicationContext myContext, Class<?> clazz) {
        return  false;
//        try {
//            Class.forName("org.apache.catalina.startup.Tomcat");
//            return false;
//        } catch (ClassNotFoundException e) {
//            return true;
//        }
    }


}
