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
public class StringHandler implements org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.TypeHandler<String> {
    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter) throws SQLException {
        ps.setString(i, parameter);
    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getString(columnName);
    }
}
