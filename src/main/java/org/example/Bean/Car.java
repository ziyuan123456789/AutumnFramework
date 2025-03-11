package org.example.Bean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPostConstruct;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPreDestroy;
import org.example.FrameworkUtils.AutumnCore.Ioc.InitializingBean;

/**
 * @author ziyuan
 * @since 2024.01
 */
@Data
@Slf4j
public class Car implements InitializingBean {
    private String name;

    @MyPostConstruct
    public void init() {
        log.info("来自@PostConstruct的初始化方法: {}孩子们,我复活了", name);
    }

    @MyPreDestroy
    public void destroy() {
        log.warn("来自@PreDestroy的销毁方法: {} 孩子们,我的时间不多了", name);
    }

    public void initMethod() {
        log.info("来自@Bean注解定义的初始化方法: {} 中的initMethod调用", name);
    }

    public void destroyMethod() {
        log.info("来自@Bean注解定义的销毁方法: {} 中的destroyMethod调用", name);
    }

    @Override
    public void afterPropertiesSet() {
        log.info("InitializingBean: {} 中的afterPropertiesSet调用", name);
    }
}
