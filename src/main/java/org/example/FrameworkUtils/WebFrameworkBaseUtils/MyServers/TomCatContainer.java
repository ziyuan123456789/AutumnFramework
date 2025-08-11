package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;


import com.autumn.mvc.WebSocket.DispatchWebSocketServlet;
import com.autumn.mvc.WebSocket.WebSocketConfigurator;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.ContextRefreshedEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationListener;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.EnvironmentAware;
import org.example.FrameworkUtils.AutumnCore.env.Environment;

import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;


/**
 * @author ziyuan
 */

@Slf4j
@MyComponent
public class TomCatContainer implements MyWebServer, ApplicationListener<ContextRefreshedEvent>, EnvironmentAware, BeanFactoryAware {

    private int port = 80;

    private ApplicationContext beanFactory;

    @MyAutoWired
    private DispatcherServlet dispatcherServlet;


    @Override
    public void init() throws Exception {
        log.info("切换到TomCat容器");
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        new Thread(() -> {
            try {
                Tomcat tomcat = new Tomcat();
                Connector connector = new Connector();
                connector.setPort(port);
                connector.setURIEncoding("UTF-8");
                tomcat.getService().addConnector(connector);
                Context context = tomcat.addContext("", null);
                context.addServletContainerInitializer(new org.apache.tomcat.websocket.server.WsSci(), null);
                ResourceCleanupFilter cleanupFilter = new ResourceCleanupFilter();
                FilterDef filterDef = new FilterDef();
                filterDef.setFilterName("resourceCleanupFilter");
                filterDef.setFilter(cleanupFilter);
                context.addFilterDef(filterDef);
                FilterMap filterMap = new FilterMap();
                filterMap.setFilterName("resourceCleanupFilter");
                filterMap.addURLPattern("/*");
                context.addFilterMap(filterMap);
                Tomcat.addServlet(context, "dispatcherServlet", dispatcherServlet);
                context.addServletMappingDecoded("/", "dispatcherServlet");
                tomcat.start();
                ServerContainer serverContainer = (ServerContainer)
                        context.getServletContext().getAttribute(ServerContainer.class.getName());

                DispatchWebSocketServlet dispatcherEndpoint = beanFactory.getBean(DispatchWebSocketServlet.class);
                WebSocketConfigurator configurator = new WebSocketConfigurator(dispatcherEndpoint);
                for (String path : dispatcherEndpoint.getController().keySet()) {
                    ServerEndpointConfig config = ServerEndpointConfig.Builder
                            .create(DispatchWebSocketServlet.class, path)
                            .configurator(configurator)
                            .build();
                    serverContainer.addEndpoint(config);
                    log.info("开始注册WebSocket端点:{}", path);
                }
                log.info("接受到{}信号,服务于{}端口启动", event.getClass().getSimpleName(), port);
                log.info("http://localhost:{}/", port);
                tomcat.getServer().await();
            } catch (LifecycleException | DeploymentException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    @Override
    public boolean supportsEvent(ApplicationEvent event) {
        return event instanceof ContextRefreshedEvent;
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.port = Integer.parseInt(environment.getProperty("port"));
    }

    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.beanFactory = beanFactory;
    }
}
