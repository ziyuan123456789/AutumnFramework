package org.example.FrameworkUtils.AutumnCore.Aop;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanDefinitionRegistry;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryPostProcessor;
import org.example.FrameworkUtils.AutumnCore.Ioc.Ordered;

/**
 * @author ziyuan
 * @since 2024.05
 */

/**
 * BeanFactoryPostProcessor一到店,所有的Bean便都看着他笑,有的叫道:BeanFactoryPostProcessor,你脸上又添上新BeanCreateException了
 * 他不回答,对着BeanFactory说:要两个BeanDefinition,要一份Environment,便排出九个ConfigurableListableBeanFactory
 * 他们又故意的高声嚷道:你一定又偷了人家的Bean了,BeanFactoryPostProcessor睁大眼睛说:你怎么这样凭空污人清白
 * 什么清白?我前天亲眼见你装成BeanDefinitionRegistryPostProcessor,向BeanDefinitionRegistry加BeanDefinition,被BeanFactory吊着打
 * BeanFactoryPostProcessor便涨红了脸,额上的青筋条条绽出,争辩道:
 * 窃Bean不能算偷……那叫注册！……开发者的事,能算偷么?接连便是难懂的话
 * 什么原初的后置处理器,什么postProcessBeanFactory,什么一切都没有的前提下还能依赖注入
 * 引得众人都哄笑起来：Factory内外充满了快活的空气
 */
@Slf4j
public class JokePostProcessor implements BeanFactoryPostProcessor, Ordered {

    @Override
    public void postProcessBeanFactory(AnnotationScanner scanner, BeanDefinitionRegistry registry) throws Exception {
        log.info("{} 从配置文件或自动装配机制加载,提前干预BeanDefinition的生成,优先级为Ordered,同时实现了BeanFactoryPostProcessor接口", this.getClass().getSimpleName());
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

    @Override
    public int getOrder() {
        return 1;
    }
}
