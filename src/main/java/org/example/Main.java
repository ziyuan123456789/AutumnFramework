package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyContext;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnFrameworkRunner;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Session.SessionManager;

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
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        List<String> jvmArgs = runtimeBean.getInputArguments();

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


    }








}