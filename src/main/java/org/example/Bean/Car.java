package org.example.Bean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPostConstruct;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPreDestroy;

/**
 * @author ziyuan
 * @since 2024.01
 */
@Data
@Slf4j
public class Car {
    private String name;

    @MyPostConstruct
    public void init() {
        log.warn("{}{} 孩子们,我复活了", this.getClass().getSimpleName(), name);
    }

    @MyPreDestroy
    public void destroy() {
        log.warn("{}{} 孩子们,我的时间不多了", this.getClass().getSimpleName(), name);
    }

}
