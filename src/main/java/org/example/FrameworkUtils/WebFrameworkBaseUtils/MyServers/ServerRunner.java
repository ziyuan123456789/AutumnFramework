package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyComponent;

@MyComponent
@Slf4j
public class ServerRunner {
    @MyAutoWired
    MyServer server;

    public void run() {
        try {
            server.init();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }

}
