package annotation.scan.test;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;

/**
 * @author ziyuan
 * @since 2025.02
 */

//测试类 用于看看@ComponentScan是否生效
@Slf4j
@MyComponent
public class AnnotationScan {
    static {
        log.info("AnnotationScan init");
    }

}
