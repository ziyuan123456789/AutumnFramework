package org.example.service.impl;

import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyService;
import org.example.service.CycleService;
import org.example.service.TestService;

/**
 * @author ziyuan
 * @since 2023.12
 */
@MyService
public class CycleServiceImpl implements CycleService {
    @MyAutoWired
    TestService testService;
    @Override
    public void cycle() {
        System.out.println("循环依赖");
    }
}
