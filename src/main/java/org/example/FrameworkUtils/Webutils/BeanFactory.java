package org.example.FrameworkUtils.Webutils;

/**
 * @author wangzhiyi
 * @since 2023.10
 */
public interface BeanFactory {
    String FACTORY_BEAN_PREFIX = "&";




    <T> T getBean(Class<T> var1);

    void setBean(Class var1);

    Class<?> getType(String var1);

}
