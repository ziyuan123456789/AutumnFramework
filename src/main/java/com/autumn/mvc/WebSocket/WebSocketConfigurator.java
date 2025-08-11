package com.autumn.mvc.WebSocket;


import javax.websocket.Endpoint;
import javax.websocket.server.ServerEndpointConfig;

/**
 *
 *
 * @author ziyuan
 * @since 2025.08
 */
public class WebSocketConfigurator extends ServerEndpointConfig.Configurator {

    private final Endpoint endpointInstance;

    public WebSocketConfigurator(Endpoint endpointInstance) {
        this.endpointInstance = endpointInstance;
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        if (endpointClass.isInstance(this.endpointInstance)) {
            return (T) this.endpointInstance;
        }
        throw new InstantiationException("创建失败");
    }
}
