package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.service.Test2Service;
import org.example.service.TestService;

/**
 * @author ziyuan
 * @since 2023.10
 */
@MyController
@Slf4j
public class DataController {

    @MyAutoWired
    TestService testService;

    @MyAutoWired
    Test2Service test2Service;

}
