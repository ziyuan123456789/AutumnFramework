package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnFrameworkRunner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Slf4j
/**
 * @author wangzhiyi
 * @since 2023.10
 */


public class Main {
    public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        AutumnFrameworkRunner autumnFrameworkRunner=new AutumnFrameworkRunner();
        autumnFrameworkRunner.run(Main.class, args);
    }
}