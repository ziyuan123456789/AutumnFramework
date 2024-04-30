package org.example.service.impl;

import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyService;
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


    @Override
    public String print(String s) {
        return testService.print("=====");
    }
}
