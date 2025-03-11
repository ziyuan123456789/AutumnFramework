package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;


import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.IocInitEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationListener;
import org.example.FrameworkUtils.AutumnCore.Ioc.EnvironmentAware;
import org.example.FrameworkUtils.AutumnCore.env.Environment;


/**
 * @author ziyuan
 */

@Slf4j
@MyComponent
public class TomCatContainer implements MyServer, ApplicationListener<IocInitEvent>, EnvironmentAware {

    private int port;

    @MyAutoWired
    private DispatcherServlet dispatcherServlet;

    @Override
    public void init() throws Exception {
        log.info("切换到TomCat容器");
    }


    @Override
    public void onApplicationEvent(IocInitEvent event) {
        new Thread(() -> {
            try {
                Tomcat tomcat = new Tomcat();
                Connector connector = new Connector();
                connector.setPort(port);
                connector.setURIEncoding("UTF-8");
                tomcat.getService().addConnector(connector);
                Context context = tomcat.addContext("/", null);
                Tomcat.addServlet(context, "dispatcherServlet", dispatcherServlet);
                context.addServletMappingDecoded("/", "dispatcherServlet");
                tomcat.start();
                log.info("服务于{}端口启动", port);
                log.info("http://localhost:{}/", port);
                tomcat.getServer().await();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }).start();

    }

    @Override
    public boolean supportsEvent(ApplicationEvent event) {
        return event instanceof IocInitEvent;
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.port = Integer.parseInt(environment.getProperty("port"));
    }
}
