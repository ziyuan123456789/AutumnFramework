package org.example.Controller;

import com.autumn.mvc.CrossOrigin;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.service.AsyncService;

/**
 * @author ziyuan
 * @since 2025.06
 */
@MyController
@MyRequestMapping("/async")
@Slf4j
public class AutumnAsyncController {

    @MyAutoWired
    private AsyncService asyncService;


    //测试异步能力
    @CrossOrigin("*")
    @MyRequestMapping("/")
    public String asyncTest() {
        asyncService.asyncTest();
        return "异步测试";
    }


}
