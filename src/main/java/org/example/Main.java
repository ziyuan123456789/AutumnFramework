package org.example;

import org.example.FrameworkUtils.AutumnCore.Annotation.EnableAutoSpiConfiguration;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnFrameworkRunner;


/*
  @author ziyuan
 * @since 2023.10
 */
@MyConfig
@EnableAutoSpiConfiguration
//@EnableAutumnFramework
public class Main {
    public static void main(String[] args) {
      AutumnFrameworkRunner autumnFrameworkRunner=new AutumnFrameworkRunner();
      autumnFrameworkRunner.run(Main.class);
    }


}