package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;


import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.ConditionCheck.TomCatConditionCheck;

@MyConfig
@MyConditional(TomCatConditionCheck.class)
@Slf4j
public class TomCatContainer implements MyServer {
    @Override
    public void init() throws Exception {
        log.info("切换到TomCat容器");
//        class dispatcherservlet{
//            void init() throws Exception{
//
//            }
//
//        }
        //xxx:后续完成
    }
}
