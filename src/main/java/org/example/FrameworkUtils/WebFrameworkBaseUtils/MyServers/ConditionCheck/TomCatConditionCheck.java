package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.ConditionCheck;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyCondition;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyContext;

@MyComponent
@Slf4j
public class TomCatConditionCheck  implements MyCondition  {
    @Override
    public void init() {
        log.info(this.getClass().getSimpleName()+"条件处理器中的初始化方法被执行");
    }

    @Override
    public boolean matches(MyContext myContext, Class<?> clazz) {
        try {
            Class.forName("org.apache.catalina.startup.Tomcat");
            log.info("TomCat启动!");
            return true;
        } catch (ClassNotFoundException e) {
            log.info("无TomCat依赖,使用手搓的Socket服务器提供web支持");
            return false;
        }
    }
}
