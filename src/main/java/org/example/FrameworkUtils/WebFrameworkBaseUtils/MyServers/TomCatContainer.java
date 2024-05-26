package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;


import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.example.FrameworkUtils.AutumnCore.Annotation.Import;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;
import org.example.FrameworkUtils.AutumnCore.Ioc.MyContext;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.ConditionCheck.TomCatConditionCheck;


@MyConfig
@MyConditional(TomCatConditionCheck.class)
@Slf4j
@Import(DispatcherServlet.class)
public class TomCatContainer implements MyServer {
    @Value("port")
    int port;

    @Override
    public void init() throws Exception {
        log.info("切换到TomCat容器");
        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector();
        connector.setPort(port);
        connector.setURIEncoding("UTF-8");
        tomcat.getService().addConnector(connector);
        Context context = tomcat.addContext("/", null);
        DispatcherServlet servlet = (DispatcherServlet) MyContext.getInstance().getBean(DispatcherServlet.class.getName());
        Tomcat.addServlet(context, "dispatcherServlet", servlet);
        context.addServletMappingDecoded("/", "dispatcherServlet");
        log.info("服务于{}端口启动", port);
        log.info("http://localhost:{}/", port);
        tomcat.start();
        tomcat.getServer().await();

    }
}
