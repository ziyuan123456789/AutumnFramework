package org.example.Config;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Event.Event;
import org.example.FrameworkUtils.AutumnCore.Event.IocInitEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.EventListener;
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
public class UserBeanPostProcessor implements BeanPostProcessor, Ordered, EventListener<IocInitEvent> {

    private final Map<String, Long> beanInitializationTimes = new ConcurrentHashMap<>();
    private final Map<String, Long> beanStartTimes = new ConcurrentHashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        long startTime = System.currentTimeMillis();
        beanStartTimes.put(beanName, startTime);
        log.info("before -- {}", beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        long endTime = System.currentTimeMillis();
        long startTime = beanStartTimes.get(beanName);
        long initializationTime = endTime - startTime;
        beanInitializationTimes.put(beanName, initializationTime);
        log.info("after -- {} (花费 {} ms)", beanName, initializationTime);
        return bean;
    }

    @Override
    public int getOrder() {
        return 10;
    }

    public List<Map.Entry<String, Long>> getTop10LongestInitializationBeans() {
        return beanInitializationTimes.entrySet().stream()
                .sorted((entry, entry2) -> Long.compare(entry2.getValue(), entry.getValue()))
                .limit(10)
                .toList();
    }


    @Override
    public void onEvent(IocInitEvent event) {
        List<Map.Entry<String, Long>> top10Beans = getTop10LongestInitializationBeans();
        log.info("=====耗时最长的10个BEAN======");
        top10Beans.forEach(entry -> log.info("Bean: {} 花费 {} ms", entry.getKey(), entry.getValue()));
        log.info("==========================");
    }

    @Override
    public boolean supportsEvent(Event event) {
        return event instanceof IocInitEvent;
    }
}
