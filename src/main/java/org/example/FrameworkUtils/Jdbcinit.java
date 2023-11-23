package org.example.FrameworkUtils;



import org.example.FrameworkUtils.Annotation.MyComponent;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import java.util.Properties;

/**
 * @author wsh
 */
@MyComponent
public class Jdbcinit {

    public  ResultSet querySql(String sql) throws SQLException {
        Properties properties=initProperties();
        Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("user"), properties.getProperty("password"));
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }
    public  String[] getProperties()  {
        return Objects.requireNonNull(initProperties()).getProperty("field").split(",");
    }
    public   Properties initProperties(){
        Properties properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/test.properties");
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return properties;
    }

}
