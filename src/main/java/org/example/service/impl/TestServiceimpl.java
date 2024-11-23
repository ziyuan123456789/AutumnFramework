package org.example.service.impl;


import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyService;
import org.example.service.CycleService;
import org.example.service.Test2Service;
import org.example.service.TestService;

/**
 * @author ziyuan
 * @since 2023.10
 */
@MyService
public class TestServiceimpl implements TestService {

    @MyAutoWired
    Test2Service test2Service;

    @MyAutoWired
    CycleService cycleService;

    @Override
    public String print(String s) {
        System.out.println(s);
        return  s;
    }

    @Override
    public void cycle() {
        System.out.println("循环依赖");
    }


}
