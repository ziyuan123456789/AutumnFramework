package org.example.FrameworkUtils.Exception.OrmError;

/**
 * @author ziyuan
 * @since 2023.11
 */
public class JdbcInitException extends RuntimeException {
    public JdbcInitException() {
        super("Jdbc初始化失败,检查配置文件是否正确");
    }

    public JdbcInitException(String message) {
        super(message);
    }
}
