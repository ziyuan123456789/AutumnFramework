package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;


import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConfig;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.ConditionCheck.TomCatConditionCheck;

@MyConfig
@MyConditional(TomCatConditionCheck.class)
public class TomCatContainer implements MyServer {
    @Override
    public void init() throws Exception {
        System.out.println("切换到TomCat容器");
    }
}
