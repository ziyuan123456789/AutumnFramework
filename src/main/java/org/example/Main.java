package org.example;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnFrameworkRunner;
import org.example.FrameworkUtils.Session.SessionManager;
import org.example.FrameworkUtils.Webutils.MyContext;

@Slf4j
/**
 * @author ziyuan
 * @since 2023.10
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("\n                _                         __  ____      _______ \n" +
                "     /\\        | |                       |  \\/  \\ \\    / / ____|\n" +
                "    /  \\  _   _| |_ _   _ _ __ ___  _ __ | \\  / |\\ \\  / / |     \n" +
                "   / /\\ \\| | | | __| | | | '_ ` _ \\| '_ \\| |\\/| | \\ \\/ /| |     \n" +
                "  / ____ \\ |_| | |_| |_| | | | | | | | | | |  | |  \\  / | |____ \n" +
                " /_/    \\_\\__,_|\\__|\\__,_|_| |_| |_|_| |_|_|  |_|   \\/   \\_____|\n" +
                "                                                                \n" +
                "                                                                ");
        AutumnFrameworkRunner autumnFrameworkRunner=new AutumnFrameworkRunner();
        autumnFrameworkRunner.run(Main.class, args);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            MyContext myContext = MyContext.getInstance();
            SessionManager sessionManager = (SessionManager) myContext.getBean(SessionManager.class);
            sessionManager.exitSave();
        }));

    }

}