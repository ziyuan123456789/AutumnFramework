package org.example.Config;

import org.example.Bean.Temp;
import org.example.FrameworkUtils.Annotation.AutunmnBean;
import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Annotation.MyConfig;
import org.example.FrameworkUtils.Annotation.MyService;
import org.example.mapper.TestMapper;

/**
 * @author wangzhiyi
 * @since 2023.10
 */
@MyConfig
public class CrossOriginConfig {
    @MyAutoWired
    TestMapper testMapper;
    @AutunmnBean
    public Temp beanTest(){
        return  testMapper.selectById1(1);
    }
}
