package com.autumn.mvc.WebSocket;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.InitializingBean;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2025.08
 */


/**
 * WebSocket的调度器
 * 跟据请求的路径找到对应的WebSocket端点
 * 并将消息处理委托给对应的端点
 * 这个Bean依靠SPI创建,在autumn.factors中声明
 */
@Slf4j
public class DispatchWebSocketServlet extends Endpoint implements InitializingBean, BeanFactoryAware {

    @Getter
    private final Map<String, WebSocketEndpoint> controller = new HashMap<>();

    private ApplicationContext context;


    @Override
    public void onOpen(Session session, EndpointConfig config) {
        String path = session.getRequestURI().getPath();
        if (!(path == null || path.isEmpty())) {
            WebSocketEndpoint controller = this.controller.get(path);
            if (controller != null) {
                session.addMessageHandler(String.class, controller::onMsg);
                controller.onOpen();
            } else {
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "没有找到对应的端点: " + path));
                } catch (Exception e) {
                    log.error("关闭发生了错误: {}", path, e);
                }
            }

        }

    }


    @Override
    public void onClose(Session session, CloseReason closeReason) {
        String path = session.getRequestURI().getPath();
        WebSocketEndpoint controller = this.controller.get(path);
        if (controller != null) {
            controller.onClose();
        } else {
            log.warn("未知端点: {}", path);
        }
    }

    @Override
    public void onError(Session session, Throwable thr) {
        String path = session.getRequestURI().getPath();
        WebSocketEndpoint controller = this.controller.get(path);
        if (controller != null) {
            log.error("WebSocket端点发生错误: {}", path, thr);
        } else {
            log.warn("未知端点发生错误: {}", path, thr);
        }
    }


    @Override
    public void afterPropertiesSet() {
        for (MyBeanDefinition mb : context.getBeanDefinitionMap().values()) {
            Class<?> clazz = mb.getBeanClass();
            if (clazz.isAnnotationPresent(MyWebSocketEndpoint.class)) {
                if (!WebSocketEndpoint.class.isAssignableFrom(clazz)) {
                    throw new IllegalArgumentException("WebSocket端点必须实现WebSocketEndpoint接口:" + clazz.getName());
                }
                MyWebSocketEndpoint annotation = clazz.getAnnotation(MyWebSocketEndpoint.class);
                String path = annotation.value();
                if (path.isEmpty()) {
                    throw new IllegalArgumentException("WebSocket端点不得为空");
                }
                WebSocketEndpoint webSocketEndpoint = (WebSocketEndpoint) context.getBean(mb.getName());
                controller.put(path, webSocketEndpoint);
            }
        }

    }

    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.context = beanFactory;
    }

}
