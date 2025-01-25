package org.example.FrameworkUtils.AutumnCore.env;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/*
  @author ziyuan
 * @since 2025.01
 */

/**
 * 环境确实会改变人,当你知道年终奖鸡毛没发的时候,你就想run了
 */
@Slf4j
public class DefaultEnvironment implements ConfigurableEnvironment {

    private final ApplicationArguments applicationArguments;
    private final PropertySources propertySources;
    private final Properties properties;
    private String[] activeProfiles;
    private String[] defaultProfiles;

    public DefaultEnvironment(ApplicationArguments applicationArguments) {
        this.applicationArguments = applicationArguments;
        this.propertySources = new PropertySources();
        this.properties = new Properties();
        this.activeProfiles = new String[0];
        this.defaultProfiles = new String[0];
        this.setDefaultProfiles(DEFAULT_PROFILE);
        this.setActiveProfiles(DEFAULT_PROFILE);

    }

    @Override
    public ApplicationArguments getApplicationArguments() {
        return applicationArguments;
    }

    @Override
    public String getProperty(String key) {
        for (Map.Entry<String, Properties> entry : propertySources.getAllPropertySources().entrySet()) {
            String value = entry.getValue().getProperty(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public Properties getAllProperties() {
        return properties;
    }

    @Override
    public String[] getActiveProfiles() {
        return activeProfiles;
    }

    @Override
    public void setActiveProfiles(String... profiles) {
        this.activeProfiles = profiles;
    }

    @Override
    public String[] getDefaultProfiles() {
        return defaultProfiles;
    }

    @Override
    public void setDefaultProfiles(String... profiles) {
        this.defaultProfiles = profiles;
    }

    public void loadConfiguration(String filename,String propertySourceName) {
        try (FileInputStream fileInputStream = new FileInputStream(filename)) {
            properties.load(fileInputStream);
            propertySources.addPropertySource(propertySourceName, properties);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void addActiveProfile(String profile) {
        String[] newProfiles = new String[activeProfiles.length + 1];
        System.arraycopy(activeProfiles, 0, newProfiles, 0, activeProfiles.length);
        newProfiles[activeProfiles.length] = profile;
        activeProfiles = newProfiles;
    }

    @Override
    public PropertySources getPropertySources() {
        return propertySources;
    }

    @Override
    public Map<String, Object> getSystemProperties() {
        return System.getProperties().entrySet().stream()
                .collect(Collectors.toMap(entry -> (String) entry.getKey(), Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> getSystemEnvironment() {
        return System.getenv().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}