package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.ConditionCheck;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnMVC.MyCondition;
import org.example.FrameworkUtils.AutumnMVC.MyContext;
@MyComponent
@Slf4j
public class SocketServerConditionCheck implements MyCondition {
    @Override
    public void init() {
        log.info(this.getClass().getSimpleName()+"条件处理器中的初始化方法被执行");
    }

    @Override
    public boolean matches(MyContext myContext, Class<?> clazz) {
        return true;
    }

    @Override
    public void after() {

        MyCondition.super.after();
    }
}
