package org.example;

import lombok.extern.slf4j.Slf4j;
import org.AutumnAP.EnableAutumnFramework;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnFrameworkRunner;


/*
  @author ziyuan
 * @since 2023.10
 */
@MyConfig
@Slf4j
//@EnableAutumnFramework
public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        //xxx:解除EnableAutumnFramework的注解并删掉下面的代码,一样可以运行,但是import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnFrameworkRunner;要保留
        //xxx:我的技术力有限,整不明白怎么在编译期导包
        AutumnFrameworkRunner runner = new AutumnFrameworkRunner();
        runner.run(Main.class);


    }

}