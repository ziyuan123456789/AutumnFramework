package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;


import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConditional;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.ConditionCheck.TomCatConditionCheck;


/**
 * @author wsh
 */
@MyConfig
@MyConditional(TomCatConditionCheck.class)
@Slf4j
public class TomCatContainer implements MyServer, BeanFactoryAware {
    @Value("port")
    int port;
    private ApplicationContext beanFactory;

    @Override
    public void init() throws Exception {
        log.info("切换到TomCat容器");
        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector();
        connector.setPort(port);
        connector.setURIEncoding("UTF-8");
        tomcat.getService().addConnector(connector);
        Context context = tomcat.addContext("/", null);
        DispatcherServlet servlet = (DispatcherServlet) beanFactory.getBean(DispatcherServlet.class.getName());
        Tomcat.addServlet(context, "dispatcherServlet", servlet);
        //xxx:进行简单路由操作,所有请求都交给dispatcherServlet处理
        context.addServletMappingDecoded("/", "dispatcherServlet");
        log.info("服务于{}端口启动", port);
        log.info("http://localhost:{}/", port);
        tomcat.start();
        tomcat.getServer().await();

    }

    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.beanFactory = beanFactory;
    }
}
