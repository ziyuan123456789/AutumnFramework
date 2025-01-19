package org.example.FrameworkUtils.AutumnCore.env;

import java.util.List;
import java.util.Properties;

/**
 * @author ziyuan
 * @since 2025.01
 */
public interface ApplicationArguments {
    String[] getArgs();

    Properties getSystemProperties();

    List<String> getVmProperties();

    String[] getCommandLineArgs();
}
