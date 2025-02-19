package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;


import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.IocInitEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationListener;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.EnvironmentAware;
import org.example.FrameworkUtils.AutumnCore.env.Environment;


/**
 * @author ziyuan
 */

@Slf4j
@MyComponent
public class TomCatContainer implements MyServer, ApplicationListener<IocInitEvent>, BeanFactoryAware, EnvironmentAware {

    private int port;

    /**
     * 这个地方不能进行依赖注入,因为MyServer被servletWebServerApplicationContext的onRefresh提前实例化
     * 提前实例化的Bean依赖注入会出现问题
     * 所以请使用aware接口拿到beanFactory自己getBean
     */
//    @MyAutoWired
//    private DispatcherServlet dispatcherServlet;

    private ApplicationContext beanFactory;

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
                Tomcat.addServlet(context, "dispatcherServlet", beanFactory.getBean(DispatcherServlet.class));
                context.addServletMappingDecoded("/", "dispatcherServlet");
                log.info("服务于{}端口启动", port);
                log.info("http://localhost:{}/", port);
                tomcat.start();
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
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.port = Integer.parseInt(environment.getProperty("port"));
    }
}
