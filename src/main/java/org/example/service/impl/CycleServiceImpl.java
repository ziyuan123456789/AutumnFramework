package org.example.service.impl;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyService;
import org.example.service.CycleService;

/**
 * @author ziyuan
 * @since 2023.12
 */
@MyService
public class CycleServiceImpl implements CycleService {

    @MyAutoWired
    private CycleService cycleService;

    @Override
    public void cycle() {
        System.out.println("循环依赖");
    }
}
