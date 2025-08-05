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
 * 用于加载Autumn框架的配置文件和自动配置文件
 * 解析META-INF/autumn/autumn.factories和META-INF/autumn/AutoConfiguration.imports
 * 将配置项存储在Map中，便于后续使用
 */
@Slf4j
public class AutumnFactoriesLoader {

    private static final String FACTORIES_RESOURCE_LOCATION = "META-INF/autumn/autumn.factories";

    private static final String AUTOCONFIGURATION_RESOURCE_LOCATION = "META-INF/autumn/AutoConfiguration.imports";


    private static Map<String, List<String>> simpleBeans;


    private static List<String> autoConfigList;


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
        simpleBeans = configMap;
        return configMap;
    }

    public static List<String> parseAutoConfigurations() throws IOException {
        List<String> configList = new ArrayList<>();
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(AUTOCONFIGURATION_RESOURCE_LOCATION);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            try (InputStream is = url.openStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        configList.add(line.trim());
                    }
                }
            }
        }
        autoConfigList = configList;
        return configList;
    }

    public static Map<String, List<String>> getSimpleBeans() {
        if (simpleBeans == null) {
            try {
                parseConfigurations();
            } catch (Exception e) {
                throw new RuntimeException("加载工厂文件失败", e);
            }
        }
        return simpleBeans;
    }

    public static List<String> getAutoConfigList() {

        if (autoConfigList == null) {
            try {
                parseAutoConfigurations();
            } catch (Exception e) {
                throw new RuntimeException("加载自动配置文件失败", e);
            }
        }
        return autoConfigList;
    }
}
