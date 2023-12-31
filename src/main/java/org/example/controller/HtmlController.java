package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.Bean.User;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyController;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyRequestMapping;
import org.example.Bean.Temp;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyRequest;
import org.example.mapper.TestMapper;

import java.util.List;

/**
 * @author ziyuan
 * @since 2023.10
 */
@Slf4j
@MyController


public class HtmlController {
    @MyAutoWired
    TestMapper testmapper;

    @MyAutoWired
    User u;

    @MyRequestMapping("/index")
    public List<Temp> gotoMainHtml(MyRequest myRequest) {
        log.error(getClass().getName() + "@" + Integer.toHexString(hashCode()));
        return testmapper.selectById(1);
    }

    @MyRequestMapping("/123")
    public String iio(MyRequest myRequest) {
        return u.toString();
    }

}
