package org.example.FrameworkUtils.Webutils.ThreadWatcher;

import lombok.extern.slf4j.Slf4j;
import org.example.Main;
import org.example.FrameworkUtils.AutumnFrameworkRunner;

/**
 * @author ziyuan
 * @since 2023.10
 */
@Slf4j
public class ThreadWatcher implements  Watcher{
    @Override
    public void getmessage(String message) {
        System.out.println("进程死亡");
        System.out.println(message);
        AutumnFrameworkRunner autumnFrameworkRunner=new AutumnFrameworkRunner();
        autumnFrameworkRunner.run(Main.class, null);
    }
}
