package org.example.FrameworkUtils.AutumnCore.BeanLoader;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ziyuan
 * @since 2024.07
 */

/**
 * 宇宙大爆炸和SPI到底哪个更重要?
 * 据一个活得很久的黑框眼镜长者说,当年上帝就是用SPI创造了宇宙,其实宇宙就是一个autumn.factories中的配置文件
 */
@Slf4j
public class AutumnFactoriesLoader {

    private static final String FACTORIES_RESOURCE_LOCATION = "META-INF/autumn/autumn.factories";

    private static final String AUTOCONFIGURATION_RESOURCE_LOCATION = "META-INF/autumn/AutoConfiguration.imports";

    public static Map<String, List<String>> parseConfigurations() throws IOException {
        Map<String, List<String>> configMap = new ConcurrentHashMap<>();
        configMap.put("BeanFactoryPostProcessor", new ArrayList<>());
        configMap.put("BootstrapRegistryInitializer", new ArrayList<>());
        configMap.put("ApplicationContextInitializer", new ArrayList<>());
        configMap.put("ApplicationListener", new ArrayList<>());
        configMap.put("AutumnApplicationRunListener", new ArrayList<>());
        configMap.put("Beans", new ArrayList<>());

        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(FACTORIES_RESOURCE_LOCATION);

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

    public static Map<String, List<String>> parseAutoConfigurations() throws IOException {
        Map<String, List<String>> configMap = new ConcurrentHashMap<>();
        configMap.put("BeanDefinitionRegistryPostProcessor", new ArrayList<>());
        configMap.put("BeanFactoryPostProcessor", new ArrayList<>());

        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(AUTOCONFIGURATION_RESOURCE_LOCATION);

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
                            if (configMap.containsKey(type)) {
                                configMap.get(type).add(implementation);
                            }
                        }
                    }
                }
            }
        }

        return configMap;
    }
}
