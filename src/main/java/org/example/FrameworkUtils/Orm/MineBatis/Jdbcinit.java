package org.example.FrameworkUtils.Orm.MineBatis;


import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyComponent;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author wsh
 */
@Slf4j
@MyComponent
public class Jdbcinit {

    public  ResultSet querySql(String sql) throws SQLException {
        Properties properties=initProperties();
        Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("user"), properties.getProperty("password"));
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }
    public <T> T executeUpdate(String sql, Class<T> clazz) throws SQLException {
        Properties properties = initProperties();
        try (Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("user"), properties.getProperty("password"));
             Statement statement = connection.createStatement()) {
            int affectedRows=0;
            try{
                affectedRows = statement.executeUpdate(sql);
            }catch (SQLIntegrityConstraintViolationException e){
                log.info("SQL执行失败,检查主键约束",e);
            }
            if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
                return clazz.cast(affectedRows);
            } else if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
                return clazz.cast(affectedRows > 0);
            } else {
                throw new IllegalArgumentException("错误的返回值类型,只接受int/Integer/Boolean/boolean");
            }
        } catch (Exception e) {
            throw new SQLException("更新出错", e);
        }
    }


    public   Properties initProperties(){
        Properties properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/test.properties");
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return properties;
    }

}
