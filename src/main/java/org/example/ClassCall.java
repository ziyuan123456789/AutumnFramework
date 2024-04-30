package org.example;


import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.MyContext;

/**
 * @author ziyuan
 * @since 2024.04
 */
@Slf4j
public class ClassCall {
    private static  MyContext myContext = MyContext.getInstance();
    static {
        log.error("谁叫我了?");
    }
}
