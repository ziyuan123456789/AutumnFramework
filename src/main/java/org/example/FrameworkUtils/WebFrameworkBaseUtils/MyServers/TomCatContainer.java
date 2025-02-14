package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;


import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;
import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.IocInitEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationListener;


/**
 * @author wsh
 */
@MyComponent
@Slf4j
public class TomCatContainer implements MyServer, ApplicationListener<IocInitEvent> {

    @Value("port")
    private int port;


//    @MyAutoWired
//    private DispatcherServlet dispatcherServlet;

    @Override
    public void init() throws Exception {
        log.info("切换到TomCat容器");
    }


    @Override
    public void onApplicationEvent(IocInitEvent event) {
//        new Thread(() -> {
//            try {
//                Tomcat tomcat = new Tomcat();
//                Connector connector = new Connector();
//                connector.setPort(port);
//                connector.setURIEncoding("UTF-8");
//                tomcat.getService().addConnector(connector);
//                Context context = tomcat.addContext("/", null);
//                Tomcat.addServlet(context, "dispatcherServlet", dispatcherServlet);
//                //xxx:进行简单路由操作,所有请求都交给dispatcherServlet处理
//                context.addServletMappingDecoded("/", "dispatcherServlet");
//                log.info("服务于{}端口启动", port);
//                log.info("http://localhost:{}/", port);
//                tomcat.start();
//                tomcat.getServer().await();
//
//            } catch (Exception e) {
//                log.error(e.getMessage(), e);
//            }
//        }).start();

    }

    @Override
    public boolean supportsEvent(ApplicationEvent event) {
        return event instanceof IocInitEvent;
    }
}
