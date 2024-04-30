package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConfig;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnFrameworkRunner;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

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

//        AutumnFrameworkRunner autumnFrameworkRunner=new AutumnFrameworkRunner();
//        autumnFrameworkRunner.run(Main.class);
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            MyContext myContext = MyContext.getInstance();
//            SessionManager sessionManager = myContext.getBean(SessionManager.class);
//            sessionManager.exitSave();
//        }));

    }
//    @AutumnBean
//    public String scheduled() {
//        return "autumn";
//    }

//    @AutumnBean
//    public String scheduled2() {
//        return "autumn";
//    }




}