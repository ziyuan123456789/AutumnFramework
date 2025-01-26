package org.example.FrameworkUtils.AutumnCore.BeanLoader;

import org.example.FrameworkUtils.AutumnCore.Ioc.BeanDefinitionRegistry;

/**
 * @author ziyuan
 * @since 2025.01
 */

//一个简单的外观模式
public class BeanDefinitionLoader {

    private final Object[] sources;

    private final AnnotatedBeanDefinitionReader annotatedReader;


    public BeanDefinitionLoader(BeanDefinitionRegistry registry, Object... sources) {
        this.sources = sources;
        this.annotatedReader = new AnnotatedBeanDefinitionReader(registry);
    }

    public void load() {
        for (Object source : this.sources) {
            if (source instanceof Class<?> clazz) {
                load(clazz);
            }
        }
    }

    private void load(Class<?> clazz) {
        this.annotatedReader.register(clazz);

    }


}
