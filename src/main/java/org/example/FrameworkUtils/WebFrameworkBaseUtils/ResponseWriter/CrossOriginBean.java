package org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseWriter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyPostConstruct;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyPreDestroy;

/**
 * @author ziyuan
 * @since 2023.11
 */
@Slf4j
@Data
public class CrossOriginBean {
    private String origins;

    @MyPostConstruct
    public void init() {
        log.warn("CrossOriginBean Init");
    }

    @MyPreDestroy
    public void destroy() {
        log.warn("CrossOriginBean Destroy");
    }

}
