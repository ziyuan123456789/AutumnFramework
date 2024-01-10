package org.example;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyConfig;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnFrameworkRunner;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Session.SessionManager;
import org.example.FrameworkUtils.AutumnMVC.MyContext;

@Slf4j
/*
  @author ziyuan
 * @since 2023.10
 */
@MyConfig
public class Main {
    public static void main(String[] args) {
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
        AutumnFrameworkRunner autumnFrameworkRunner=new AutumnFrameworkRunner();
        autumnFrameworkRunner.run(Main.class);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            MyContext myContext = MyContext.getInstance();
            SessionManager sessionManager = myContext.getBean(SessionManager.class);
            sessionManager.exitSave();
        }));

    }
//    @AutumnBean
//    public String scheduled() {
//        return "autumn";
//    }

//    @AutunmnBean
//    public String scheduled2() {
//        return "autumn";
//    }

}