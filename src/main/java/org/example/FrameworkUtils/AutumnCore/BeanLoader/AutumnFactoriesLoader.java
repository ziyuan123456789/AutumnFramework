package org.example.FrameworkUtils.AutumnCore.BeanLoader;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author ziyuan
 * @since 2024.07
 */
public class AutumnFactoriesLoader {
    public static void loadFactories() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> urls = classLoader.getResources("META-INF/autumn/AutoConfiguration.imports");
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Properties properties = new Properties();
                properties.load(url.openStream());

                for (String key : properties.stringPropertyNames()) {
                    String value = properties.getProperty(key);
                    System.out.println("Key: " + key + ", Value: " + value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
