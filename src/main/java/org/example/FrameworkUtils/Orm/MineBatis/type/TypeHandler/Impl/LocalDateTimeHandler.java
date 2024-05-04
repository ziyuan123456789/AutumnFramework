package org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.Impl;



import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author ziyuan
 * @since 2024.04
 */
@TypeHandler
public class LocalDateTimeHandler implements org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.TypeHandler<LocalDateTime> {

    @Override
    public void setParameter(PreparedStatement ps, int i, LocalDateTime parameter) throws SQLException {
        if (parameter != null) {
            ps.setTimestamp(i, Timestamp.valueOf(parameter));
        } else {
            ps.setTimestamp(i, null);
        }
    }

    @Override
    public LocalDateTime getResult(ResultSet rs, String columnName) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnName);
        if (timestamp != null) {
            return timestamp.toLocalDateTime();
        } else {
            return null;
        }
    }
}
