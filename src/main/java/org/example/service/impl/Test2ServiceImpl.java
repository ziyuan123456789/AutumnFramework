package org.example.service.impl;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyService;
import org.example.controller.AutumnTestController;
import org.example.service.Test2Service;
import org.example.service.TestService;


/**
 * @author ziyuan
 * @since 2023.11
 */
@MyService
public class Test2ServiceImpl implements Test2Service {

    @MyAutoWired
    TestService testService;

    @MyAutoWired
    AutumnTestController autumnTestController;


    @Override
    public String print(String s) {
        return testService.print("=====");
    }
}
