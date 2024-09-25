package org.example;

import com.autumn.async.EnableAutumnAsync;
import com.autumn.cache.EnableAutumnCache;
import com.autumn.ormstarter.minijpa.EnableJpaRepositories;
import com.autumn.transaction.annotation.EnableAutumnTransactional;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.EnableAutoConfiguration;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyPreDestroy;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnFrameworkRunner;


/*
  @author ziyuan
 * @since 2023.10
 */
@MyConfig
@EnableAutoConfiguration
@EnableAutumnAsync
@EnableAutumnCache
@EnableAutumnTransactional
@EnableJpaRepositories
@Slf4j
//@CompomentScan({"org.example"})
//@EnableAutumnFramework
/*
默认扫描Main方法所在的包也就是org.example
你可以注意到com.autumn与org.example平级,用于模拟jar包,避免Bean发现机制扫描到
 */
public class Main {
    public static void main(String[] args) {
        AutumnFrameworkRunner autumnFrameworkRunner = new AutumnFrameworkRunner();
        autumnFrameworkRunner.run(Main.class);
    }
    @MyPreDestroy
    public void sayBay(){
        log.info("再见孩子们");
    }
}

