package org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseWriter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPostConstruct;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPreDestroy;

/**
 * @author ziyuan
 * @since 2023.11
 */


//TODO: 重构这里,字典树已经准备好了,不再需要全局的CrossOrigin了
@Slf4j
@Data
public class CrossOriginBean {

    private String origins;

    @MyPostConstruct
    public void init() {
        log.info("CrossOriginBean Init");
    }

    @MyPreDestroy
    public void destroy() {
        log.info("CrossOriginBean Destroy");
    }

}
