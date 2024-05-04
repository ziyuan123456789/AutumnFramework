package org.example.FrameworkUtils.Orm.MineBatis.type;



import org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.TypeHandler;

import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.04
 */
public interface TypeHandlerRegistry {
    <T> void register(Class<T> javaType, TypeHandler<T> handler);
    <T> TypeHandler<T> getTypeHandler(Class<T> javaType);
    Map<Class<?>,TypeHandler> getTypeHandlers();
}
