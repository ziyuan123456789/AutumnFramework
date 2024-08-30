package org.example.FrameworkUtils.AutumnCore.BeanLoader;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.07
 */
@Slf4j
public class AutumnFactoriesLoader {
    public static Map<String, List<String>> parseConfigurations() throws IOException {
        Map<String, List<String>> configMap = new HashMap<>();
        configMap.put("BeanDefinitionRegistryPostProcessor", new ArrayList<>());
        configMap.put("BeanFactoryPostProcessor", new ArrayList<>());
        configMap.put("Beans", new ArrayList<>());

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> urls = classLoader.getResources("META-INF/autumn/AutoConfiguration.imports");

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            try (InputStream is = url.openStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("=")) {
                        String[] parts = line.split("=");
                        if (parts.length == 2) {
                            String type = parts[0].trim();
                            String implementation = parts[1].trim();
                            log.info("{}从META-INF配置文件自动装配", implementation);
                            if (configMap.containsKey(type)) {
                                configMap.get(type).add(implementation);
                            } else {
                                configMap.get("Beans").add(line);
                            }
                        }
                    } else {
                        configMap.get("Beans").add(line);
                    }
                }
            }
        }

        return configMap;
    }
}
