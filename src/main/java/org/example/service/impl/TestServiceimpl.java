package org.example.service.impl;


import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TestServiceimpl implements TestService {

    @MyAutoWired
    Test2Service test2Service;

    @MyAutoWired
    CycleService cycleService;

    @Override
    public String print(String s) {
        log.info(s);
        return s;
    }

    @Override
    public void cycle() {
        log.info("循环依赖");
    }


}
