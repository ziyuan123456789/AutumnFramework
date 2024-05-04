package org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.Impl;



import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author ziyuan
 * @since 2024.04
 */
@TypeHandler
public class IntegerHandler implements org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.TypeHandler<Integer> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Integer parameter) throws SQLException {
        ps.setInt(i, parameter);
    }

    @Override
    public Integer getResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getInt(columnName);
    }
}
