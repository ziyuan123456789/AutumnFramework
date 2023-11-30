package org.example;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnFrameworkRunner;
@Slf4j
/**
 * @author ziyuan
 * @since 2023.10
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("                _                         __  ____      _______ \n" +
                "     /\\        | |                       |  \\/  \\ \\    / / ____|\n" +
                "    /  \\  _   _| |_ _   _ _ __ ___  _ __ | \\  / |\\ \\  / / |     \n" +
                "   / /\\ \\| | | | __| | | | '_ ` _ \\| '_ \\| |\\/| | \\ \\/ /| |     \n" +
                "  / ____ \\ |_| | |_| |_| | | | | | | | | | |  | |  \\  / | |____ \n" +
                " /_/    \\_\\__,_|\\__|\\__,_|_| |_| |_|_| |_|_|  |_|   \\/   \\_____|\n" +
                "                                                                \n" +
                "                                                                ");
        AutumnFrameworkRunner autumnFrameworkRunner=new AutumnFrameworkRunner();
        autumnFrameworkRunner.run(Main.class, args);
    }

}