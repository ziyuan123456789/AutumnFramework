package org.example;

import com.mchange.v2.log.MLevel;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConfig;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnFrameworkRunner;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
/*
  @author ziyuan
 * @since 2023.10
 */
@MyConfig
public class Main {

    public static void main(String[] args) throws ClassNotFoundException {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> jvmArgs = runtimeMXBean.getInputArguments();

        for (String arg : jvmArgs) {
            log.warn(arg);
        }
        log.info("""

                                _                         __  ____      _______\s
                     /\\        | |                       |  \\/  \\ \\    / / ____|
                    /  \\  _   _| |_ _   _ _ __ ___  _ __ | \\  / |\\ \\  / / |    \s
                   / /\\ \\| | | | __| | | | '_ ` _ \\| '_ \\| |\\/| | \\ \\/ /| |    \s
                  / ____ \\ |_| | |_| |_| | | | | | | | | | |  | |  \\  / | |____\s
                 /_/    \\_\\__,_|\\__|\\__,_|_| |_| |_|_| |_|_|  |_|   \\/   \\_____|
                                                                               \s
                                                                                \
                """);
        AutumnFrameworkRunner autumnFrameworkRunner = new AutumnFrameworkRunner();
        autumnFrameworkRunner.run(Main.class);


//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            MyContext myContext = MyContext.getInstance();
//            SessionManager sessionManager = (SessionManager) myContext.getBean(SessionManager.class.getName());
//            log.warn("进行关机清理....请稍后");
//            sessionManager.exitSave();
//            log.warn("已成功停机");
//        }));

    }





}