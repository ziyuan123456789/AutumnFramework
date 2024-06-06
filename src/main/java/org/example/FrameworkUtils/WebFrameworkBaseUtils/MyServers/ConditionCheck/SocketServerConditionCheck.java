package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.ConditionCheck;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyCondition;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyContext;
@MyComponent
@Slf4j
public class SocketServerConditionCheck implements MyCondition {
    @Override
    public void init() {
        log.info("{}条件处理器中的初始化方法被执行", this.getClass().getSimpleName());
    }

    @Override
    public boolean matches(MyContext myContext, Class<?> clazz) {
        return  false;
//        try {
//            Class.forName("org.apache.catalina.startup.Tomcat");
//            return false;
//        } catch (ClassNotFoundException e) {
//            return true;
//        }
    }

    @Override
    public void after() {

        MyCondition.super.after();
    }
}
