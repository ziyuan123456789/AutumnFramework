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
//@EnableAutumnFramework
public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
      AutumnFrameworkRunner autumnFrameworkRunner=new AutumnFrameworkRunner();
      autumnFrameworkRunner.run(Main.class);
    }

}