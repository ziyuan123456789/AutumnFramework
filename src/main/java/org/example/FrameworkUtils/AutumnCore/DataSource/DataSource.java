package org.example.FrameworkUtils.AutumnCore.DataSource;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;

/**
 * @author ziyuan
 * @since 2024.09
 */

/**
 * 对Environment的拙劣模仿
 */
@MyComponent
public class DataSource {
    @Value("autumn.datasource.url")
    private String url;

    @Value("autumn.datasource.username")
    private String userName;

    @Value("autumn.datasource.password")
    private String password;

    @Value("autumn.datasource.driver-class-name")
    private String driver;

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getDriver() {
        return driver;
    }
}
