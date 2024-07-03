package org.example.Config;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Ioc.InstantiationAwareBeanPostProcessor;

/**
 * @author ziyuan
 * @since 2024.05
 */
@MyComponent
public class InstantiationAwareBeanPostProcessorTest implements InstantiationAwareBeanPostProcessor {


    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {


        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) {
        if(bean.getClass() == Test.class){
            ((Test) bean).setTestName("替换具体实现类");
            return false;
        }
        return true;
    }
    //xxx:BeanPostProcessor方法,选择不重写具体逻辑
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return null;
    }
}
