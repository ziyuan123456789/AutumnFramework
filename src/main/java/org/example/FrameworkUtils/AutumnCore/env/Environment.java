package org.example.FrameworkUtils.AutumnCore.env;

import java.util.Properties;

/**
 * @author ziyuan
 * @since 2025.01
 */
public interface Environment {


    ApplicationArguments getApplicationArguments();


    String getProperty(String key);


    void loadConfiguration(String filename);


    Properties getAllProperties();
}
