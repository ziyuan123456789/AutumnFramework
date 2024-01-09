package org.example.FrameworkUtils.WebFrameworkBaseUtils.ThreadWatcher;

import lombok.extern.slf4j.Slf4j;
import org.example.Main;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnFrameworkRunner;

/**
 * @author ziyuan
 * @since 2023.10
 */
@Slf4j
public class ThreadWatcher implements  Watcher{
    @Override
    public void getmessage(String message) {
        AutumnFrameworkRunner autumnFrameworkRunner=new AutumnFrameworkRunner();
        autumnFrameworkRunner.run(Main.class);
    }
}
