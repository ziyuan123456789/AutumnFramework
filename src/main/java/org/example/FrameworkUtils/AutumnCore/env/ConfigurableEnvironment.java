package org.example.FrameworkUtils.AutumnCore.env;

import java.util.Map;

/**
 * @author ziyuan
 * @since 2025.01
 */
public interface ConfigurableEnvironment extends Environment{

    String  DEFAULT_PROFILE = "autumnDefaultProperties";

    void setActiveProfiles(String... profiles);

    void setDefaultProfiles(String... profiles);

    void addActiveProfile(String profile);

    PropertySources getPropertySources();

    Map<String, Object> getSystemProperties();

    Map<String, Object> getSystemEnvironment();

}
