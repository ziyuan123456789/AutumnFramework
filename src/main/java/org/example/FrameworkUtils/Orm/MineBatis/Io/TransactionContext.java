package org.example.FrameworkUtils.Orm.MineBatis.Io;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author ziyuan
 * @since 2024.11
 */
public class TransactionContext  {

    private static final ThreadLocal<Deque<Connection>> connectionStackHolder = ThreadLocal.withInitial(ArrayDeque::new);

    public static void pushConnection(Connection connection) {
        connectionStackHolder.get().push(connection);
    }
    public static Connection popConnection() {
        return connectionStackHolder.get().pop();
    }

    public static Connection getCurrentConnection() {
        Deque<Connection> stack = connectionStackHolder.get();
        if (!stack.isEmpty()) {
            return stack.peek();
        } else {
            return null;
        }
    }

    public static void clear() {
        Deque<Connection> stack = connectionStackHolder.get();
        while (!stack.isEmpty()) {
            Connection connection = stack.pop();
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connectionStackHolder.remove();
    }
}
