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


    ApplicationArguments getApplicationArguments();


    String getProperty(String key);


    void loadConfiguration(String filename);


    Properties getAllProperties();
}
