package org.example.Config;

import lombok.Data;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.controller.AutumnTestController;

/**
 * @author ziyuan
 * @since 2024.05
 */
@MyComponent
@Data
public class Test {
    @MyAutoWired
    AutumnTestController autumnTestController;
    String testName;
}
