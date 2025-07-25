package org.example;

import com.autumn.async.EnableAutumnAsync;
import com.autumn.cache.EnableAutumnCache;
import com.autumn.mvc.EnableMVCAutoConfiguration;
import com.autumn.ormstarter.transaction.annotation.EnableAutumnTransactional;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBootApplication;
import org.example.FrameworkUtils.AutumnCore.Annotation.ComponentScan;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPreDestroy;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnApplication;


/*
  @author ziyuan
 * @since 2023.10
 */

/*
    默认扫描Main方法所在的包也就是org.example
    你可以注意到com.autumn与org.example平级,用于模拟jar包,避免Bean发现机制扫描到
 */
@Slf4j
@EnableAutumnAsync
@EnableAutumnCache
@EnableAutumnTransactional
@EnableMVCAutoConfiguration
@ComponentScan({"org.example", "annotation.scan.test"})
@AutumnBootApplication
public class Main {

    public static void main(String[] args) {
        AutumnApplication autumnApplication = new AutumnApplication(Main.class);
//        autumnApplication.addInitializers(new BaseBootstrapRegistryInitializer());
        autumnApplication.run(args);
    }

    @MyPreDestroy
    public void sayBye() {
        log.info("再见孩子们");
    }
}

