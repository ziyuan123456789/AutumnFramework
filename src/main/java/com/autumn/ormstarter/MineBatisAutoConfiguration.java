package com.autumn.ormstarter;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.Import;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Aop.JokePostProcessor;

/**
 *
 *
 * @author ziyuan
 * @since 2025.08
 */
@MyConfig
@Slf4j
@Import({SqlSessionFactoryBean.class, JokePostProcessor.class})
public class MineBatisAutoConfiguration {

    @AutumnBean
    public MineBatisStarter createMineBatisStarter() {
        return new MineBatisStarter();
    }

}
