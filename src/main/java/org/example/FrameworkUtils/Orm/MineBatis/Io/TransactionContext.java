package org.example.FrameworkUtils.Orm.MineBatis.Io;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author ziyuan
 * @since 2024.11
 */
@Slf4j
public class TransactionContext  {

    private static final ThreadLocal<Deque<Connection>> CONNECTION_HOLDER = ThreadLocal.withInitial(ArrayDeque::new);

    public static void pushConnection(Connection connection) {
        CONNECTION_HOLDER.get().push(connection);
    }
    public static Connection popConnection() {
        return CONNECTION_HOLDER.get().pop();
    }

    public static Connection getCurrentConnection() {
        Deque<Connection> stack = CONNECTION_HOLDER.get();
        if (!stack.isEmpty()) {
            return stack.peek();
        } else {
            return null;
        }
    }

    public static void clear() {
        Deque<Connection> stack = CONNECTION_HOLDER.get();
        while (!stack.isEmpty()) {
            Connection connection = stack.pop();
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("关闭连接时发生错误: {}", e.getMessage(), e);
            }
        }
        CONNECTION_HOLDER.remove();
    }
}
