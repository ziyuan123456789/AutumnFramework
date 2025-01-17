package org.example;

import com.autumn.async.EnableAutumnAsync;
import com.autumn.cache.EnableAutumnCache;
import com.autumn.ormstarter.minijpa.EnableJpaRepositories;
import com.autumn.ormstarter.transaction.annotation.EnableAutumnTransactional;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBootApplication;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPreDestroy;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnApplication;


/*
  @author ziyuan
 * @since 2023.10
 */
@EnableAutumnAsync
@EnableAutumnCache
@EnableAutumnTransactional
@EnableJpaRepositories
@AutumnBootApplication
@Slf4j
/*
默认扫描Main方法所在的包也就是org.example
你可以注意到com.autumn与org.example平级,用于模拟jar包,避免Bean发现机制扫描到
 */
public class Main {
    public static void main(String[] args) {
        AutumnApplication autumnApplication = new AutumnApplication();
        autumnApplication.run(Main.class);
    }
    @MyPreDestroy
    public void sayBay(){
        log.info("再见孩子们");
    }
}

