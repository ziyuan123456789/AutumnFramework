package org.example.Config;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.ContextFinishRefreshEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationListener;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanPostProcessor;
import org.example.FrameworkUtils.AutumnCore.Ioc.Ordered;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ziyuan
 * @since 2024.05
 */
@Slf4j
@MyComponent
public class UserBeanPostProcessor implements BeanPostProcessor, Ordered, ApplicationListener<ContextFinishRefreshEvent> {

    private final Map<String, Long> startTimeMap = new ConcurrentHashMap<>();

    private final Map<String, Long> beanLoadTimes = new ConcurrentHashMap<>();


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        startTimeMap.put(beanName, System.nanoTime());
        return bean;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Long startTime = startTimeMap.remove(beanName);
        if (startTime != null) {
            long loadTime = (System.nanoTime() - startTime) / 1_000_000;
            beanLoadTimes.put(beanName, loadTime);

        }
        return bean;
    }

    @Override
    public int getOrder() {

        return 10;
    }

    public List<Map.Entry<String, Long>> getTop10LongestInitializationBeans() {
        return beanLoadTimes.entrySet().stream()
                .sorted((entry, entry2) -> Long.compare(entry2.getValue(), entry.getValue()))
                .filter(entry -> entry.getValue() != 0)
                .limit(10)
                .toList();
    }


    @Override
    public void onApplicationEvent(ContextFinishRefreshEvent event) {
        List<Map.Entry<String, Long>> top10Beans = getTop10LongestInitializationBeans();
        log.info("=====耗时最长的BEAN======");
        top10Beans
                .forEach(entry -> log.info("Bean: {} 耗时: {} ms", entry.getKey(), entry.getValue()));
        log.info("==========================");

    }

    @Override
    public boolean supportsEvent(ApplicationEvent event) {
        return event instanceof ContextFinishRefreshEvent;
    }
}
