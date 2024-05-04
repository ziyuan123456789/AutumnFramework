package org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author ziyuan
 * @since 2024.04
 */
public interface TypeHandler<T> {
    void setParameter(PreparedStatement ps, int i, T parameter) throws SQLException;
    T getResult(ResultSet rs, String columnName) throws SQLException;
}
