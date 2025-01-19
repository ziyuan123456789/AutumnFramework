package org.example.FrameworkUtils.AutumnCore.env;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author ziyuan
 * @since 2025.01
 */
@Slf4j
public class DefaultEnvironment implements Environment {

    private final ApplicationArguments applicationArguments;
    private final Properties properties;

    public DefaultEnvironment(ApplicationArguments applicationArguments) {
        this.applicationArguments = applicationArguments;
        this.properties = new Properties();
    }

    @Override
    public ApplicationArguments getApplicationArguments() {
        return applicationArguments;
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public void loadConfiguration(String filename) {
        try (FileInputStream fileInputStream = new FileInputStream(filename)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
           log.error(e.getMessage(),e);
        }
    }

    @Override
    public Properties getAllProperties() {
        return properties;
    }
}
