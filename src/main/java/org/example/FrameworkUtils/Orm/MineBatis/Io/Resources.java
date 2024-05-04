package org.example.FrameworkUtils.Orm.MineBatis.Io;

import java.io.InputStream;

/**
 * @author ziyuan
 * @since 2024.04
 */
public class Resources {
    public static InputStream getResourceAsSteam(String path) {
        InputStream resourceAsStream = Resources.class.getClassLoader().getResourceAsStream(path);
        return resourceAsStream;
    }
}
