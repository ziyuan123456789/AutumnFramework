package org.example.FrameworkUtils.Exception;

/**
 * @author ziyuan
 * @since 2024.05
 */
public class BeanDefinitionCreationException extends RuntimeException {
    public BeanDefinitionCreationException() {
        super("BeanDefinition修改时出现问题");
    }

    public BeanDefinitionCreationException(String message) {
        super(message);
    }

    public BeanDefinitionCreationException(String s, Exception e) {
        super(s, e);

    }

}
