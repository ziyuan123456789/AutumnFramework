package org.example;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnFrameworkRunner;
import org.example.FrameworkUtils.Session.SessionManager;
import org.example.FrameworkUtils.Webutils.MyContext;

@Slf4j
/*
  @author ziyuan
 * @since 2023.10
 */
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
        autumnFrameworkRunner.run(Main.class, args);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            MyContext myContext = MyContext.getInstance();
            SessionManager sessionManager = myContext.getBean(SessionManager.class);
            sessionManager.exitSave();
        }));

    }

}