package org.example.FrameworkUtils.AutumnCore.env;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author ziyuan
 * @since 2025.01
 */

public class PropertySources {
    private final Map<String, Properties> sources = new HashMap<>();

    public void addPropertySource(String name, Properties properties) {
        sources.put(name, properties);
    }

    public Properties getPropertySource(String name) {
        return sources.get(name);
    }

    public Map<String, Properties> getAllPropertySources() {
        return sources;
    }

}