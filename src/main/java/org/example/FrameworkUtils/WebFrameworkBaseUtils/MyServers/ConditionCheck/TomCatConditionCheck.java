package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.ConditionCheck;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyCondition;

@MyComponent
@Slf4j
public class TomCatConditionCheck  implements MyCondition  {

    @Override
    public boolean matches(ApplicationContext myContext, Class<?> clazz) {
        return true;
//        try {
//            Class.forName("org.apache.catalina.startup.Tomcat");
//            log.info("TomCat启动!");
//            return true;
//        } catch (ClassNotFoundException e) {
//            log.info("无TomCat依赖,使用手搓的Socket服务器提供web支持");
//            return false;
//        }
    }
}
