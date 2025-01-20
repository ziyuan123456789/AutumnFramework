package org.example.FrameworkUtils.AutumnCore.env;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * 人人都知道ApplicationArguments,但是背后出力的DefaultApplicationArguments却没人知道
 */
public class DefaultApplicationArguments implements ApplicationArguments {
    private String[] args;
    private Properties systemProperties;
    private List<String> vmProperties;

    public DefaultApplicationArguments(String[] args) {
        this.args = args;

        this.systemProperties = System.getProperties();

        loadJvmProperties();
    }

    private void loadJvmProperties() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        vmProperties=runtimeBean.getInputArguments();
    }

    @Override
    public String[] getArgs() {
        return args;
    }

    @Override
    public Properties getSystemProperties() {
        return systemProperties;
    }

    @Override
    public List<String> getVmProperties() {
        return vmProperties;
    }

    @Override
    public String[] getCommandLineArgs() {
        return Arrays.stream(args)
                .filter(arg -> !arg.startsWith("-D"))
                .toArray(String[]::new);
    }
}