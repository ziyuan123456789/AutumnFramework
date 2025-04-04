package org.example.FrameworkUtils.Exception;

/**
 * @author ziyuan
 * @since 2023.11
 */

/**
 * BeanCreationException人缘真的很差
 */
public class BeanCreationException extends RuntimeException {
    public BeanCreationException() {
        super("Bean制造时出现问题");
    }

    public BeanCreationException(String message) {
        super(message);
    }

    public BeanCreationException(String s, Exception e) {
        super(s, e);

    }

    public BeanCreationException(Exception e) {
        super(e);
    }
}
