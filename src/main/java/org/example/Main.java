package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
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
        int[] a=new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        System.out.println(binarySearch(a,5 ));

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
    private static int binarySearch(int[] a, int t) {
        if (a == null || a.length == 0) {
            return -1;
        }
        int l = -1;
        int r = a.length;
        while (l + 1 != r) {
            int m = l + (r - l) / 2;
            if (a[m] <= t) {
                l = m;
            } else {
                r = m;
            }
        }
        return l != -1 && a[l] == t ? l : -1;
    }







}