package org.example.FrameworkUtils.AutumnCore.Ioc;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Exception.BeanCreationException;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.MyServer;

/**
 * @author ziyuan
 * @since 2025.02
 */

@Slf4j
@EqualsAndHashCode(callSuper = false)
public class AnnotationConfigServletWebServerApplicationContext extends AnnotationConfigApplicationContext {

    private MyServer myServer;

    @Override
    protected void onRefresh() {
        super.onRefresh();
        try {
            this.createWebServer();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new BeanCreationException();
        }
    }

    private void createWebServer() throws Exception {
        this.myServer = getBean(MyServer.class);
        this.myServer.init();
    }


}
