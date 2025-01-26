package org.example.FrameworkUtils.AutumnCore.BeanLoader;

import java.util.Objects;

/**
 * @author ziyuan
 * @since 2025.01
 */

public class BeanDefinitionHolder {

    private final MyBeanDefinition beanDefinition;

    private final String beanName;

    public BeanDefinitionHolder(MyBeanDefinition beanDefinition, String beanName) {
        this.beanDefinition = beanDefinition;
        this.beanName = beanName;
    }

    public BeanDefinitionHolder(MyBeanDefinition beanDefinition) {
        this.beanDefinition = beanDefinition;
        this.beanName = beanDefinition.getName();
    }

    public String getBeanName() {
        return beanName;
    }

    public MyBeanDefinition getBeanDefinition() {
        return beanDefinition;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BeanDefinitionHolder that = (BeanDefinitionHolder) o;
        return Objects.equals(beanDefinition, that.beanDefinition) && Objects.equals(beanName, that.beanName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanDefinition, beanName);
    }

}
