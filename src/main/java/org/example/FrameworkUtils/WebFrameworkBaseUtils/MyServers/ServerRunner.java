package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;

/**
 * @author wsh
 */
@MyComponent
@Slf4j
public class ServerRunner {
    @MyAutoWired
    MyWebServer server;

    public void run() {
        try {
            server.init();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }

}
