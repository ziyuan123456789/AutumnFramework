package org.example.FrameworkUtils.Orm.MineBatis.type;



import org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.TypeHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.04
 */
public class BaseTypeHandlerRegistry implements TypeHandlerRegistry {

    private Map<Class<?>, TypeHandler> typeHandlerMap = new HashMap<>();

    @Override
    public <T> void register(Class<T> javaType, TypeHandler<T> handler) {
        typeHandlerMap.put(javaType, handler);
    }

    @Override
    public <T> TypeHandler<T> getTypeHandler(Class<T> javaType) {
        return (TypeHandler<T>) typeHandlerMap.get(javaType);
    }

    @Override
    public Map<Class<?>, TypeHandler> getTypeHandlers() {
        return typeHandlerMap;
    }
}
