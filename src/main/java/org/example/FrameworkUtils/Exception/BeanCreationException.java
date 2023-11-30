package org.example.FrameworkUtils.Exception;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
public class BeanCreationException extends RuntimeException{
    public BeanCreationException() {
        super("Bean制造时出现问题");
    }

    public BeanCreationException(String message) {
        super(message);
    }

}