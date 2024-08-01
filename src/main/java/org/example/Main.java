package org.example;

import org.example.FrameworkUtils.AutumnCore.Annotation.CompomentScan;
import org.example.FrameworkUtils.AutumnCore.Annotation.EnableAutoConfiguration;
import org.example.FrameworkUtils.AutumnCore.Annotation.Import;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.Orm.MineBatis.MineBatisStarter;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnFrameworkRunner;


/*
  @author ziyuan
 * @since 2023.10
 */
@MyConfig
@EnableAutoConfiguration
@CompomentScan({"org.example"})
//@Import(MineBatisStarter.class)
//@EnableAutumnFramework
public class Main {
    public static void main(String[] args) {
      AutumnFrameworkRunner autumnFrameworkRunner=new AutumnFrameworkRunner();
      autumnFrameworkRunner.run(Main.class);
    }
}