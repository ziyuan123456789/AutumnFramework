package org.example.FrameworkUtils.AutumnCore.compare;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyOrder;
import org.example.FrameworkUtils.AutumnCore.Ioc.Ordered;
import org.example.FrameworkUtils.AutumnCore.Ioc.PriorityOrdered;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * 注解是一等公民
 */
public class AnnotationInterfaceAwareOrderComparator implements Comparator<Object> {

    private static AnnotationInterfaceAwareOrderComparator annotationInterfaceAwareOrderComparator;


    private AnnotationInterfaceAwareOrderComparator() {
    }

    public static AnnotationInterfaceAwareOrderComparator getInstance() {
        if (annotationInterfaceAwareOrderComparator == null) {
            annotationInterfaceAwareOrderComparator = new AnnotationInterfaceAwareOrderComparator();
        }
        return annotationInterfaceAwareOrderComparator;
    }

    public <T> List<T> compare(List<T> list) {
        list.sort(this);
        return list;
    }


    @Override
    public int compare(Object o1, Object o2) {
        int priority1 = getPriority(o1);
        int priority2 = getPriority(o2);

        return Integer.compare(priority1, priority2);
    }


    private int getPriority(Object obj) {
        if (obj == null) {
            return Integer.MAX_VALUE;
        }


        MyOrder myOrder = findAnnotation(obj.getClass());
        if (myOrder != null) {
            return myOrder.value();
        }


        if (obj instanceof PriorityOrdered) {
            return ((PriorityOrdered) obj).getOrder();
        }

        if (obj instanceof Ordered) {
            return ((Ordered) obj).getOrder();
        }

        return Integer.MAX_VALUE;
    }

    private <A extends Annotation> A findAnnotation(Class<?> clazz) {
        while (clazz != null && clazz != Object.class) {
            A annotation = clazz.getAnnotation((Class<A>) MyOrder.class);
            if (annotation != null) {
                return annotation;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }


}
