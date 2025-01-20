package org.example.FrameworkUtils.AutumnCore.env;

import java.util.List;
import java.util.Properties;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * ApplicationArguments最近很拽,他觉得她掌握了一切入参
 */
public interface ApplicationArguments {
    String[] getArgs();

    Properties getSystemProperties();

    List<String> getVmProperties();

    String[] getCommandLineArgs();
}
