package org.example.FrameworkUtils.AutumnCore.env;

import java.util.Properties;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * 环境会改变程序也会改变人,你觉得对吗?
 */
public interface Environment {

    String GET_MAIN_PACKAGE = "autumn.main.package";

    ApplicationArguments getApplicationArguments();

    String getProperty(String key);

    Properties getAllProperties();

    String[] getActiveProfiles();

    String[] getDefaultProfiles();

}
