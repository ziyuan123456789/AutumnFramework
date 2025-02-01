package org.example.FrameworkUtils.AutumnCore.Aop;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanDefinitionRegistry;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryPostProcessor;

/*
  @author ziyuan
 * @since 2024.05
 */

/**
 * BeanFactoryPostProcessor一到店,所有的Bean便都看着他笑,有的叫道:BeanFactoryPostProcessor,你脸上又添上新BeanCreateException了
 * 他不回答,对着context说:要两个BeanDefinition,要一份Environment,便排出九个ConfigurableListableBeanFactory
 * 他们又故意的高声嚷道:你一定又偷了人家的Bean了,BeanFactoryPostProcessor睁大眼睛说:你怎么这样凭空污人清白
 * 什么清白?我前天亲眼见你装成BeanDefinitionRegistryPostProcessor,向BeanDefinitionRegistry加BeanDefinition,被context吊着打
 * BeanFactoryPostProcessor便涨红了脸,额上的青筋条条绽出,争辩道:
 * 窃Bean不能算偷……那叫注册！……开发者的事,能算偷么?接连便是难懂的话
 * 什么原初的后置处理器,什么postProcessBeanFactory,什么一切都没有的前提下还能依赖注入
 * 引得众人都哄笑起来：context内外充满了快活的空气
 */
@Slf4j
public class JokePostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(AnnotationScanner scanner, BeanDefinitionRegistry registry) throws Exception {
        MyBeanDefinition bydBean = null;
        if (registry.containsBeanDefinition("BYD")) {
            bydBean = registry.getBeanDefinition("BYD");
        }
        if (bydBean != null) {
            bydBean.setName("postProcessChange");
            registry.removeBeanDefinition("BYD");
            registry.registerBeanDefinition("postProcessChange", bydBean);
        }
    }

}
