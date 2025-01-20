package org.example.FrameworkUtils.AutumnCore.BeanLoader;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryPostProcessor;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author ziyuan
 * @since 2024.05
 */

/**
 * XMLBeansLoader最近很生气,为什么不用她了呢? 她高性能,易表达,怎么就被
 * Annotation这个小绿茶取代了呢?
 * 其实这个问题很简单,因为Annotation更灵活,在运行时还能反差一下,灵活的底线谁不喜欢呢?
 * 你看看,不灵活的XMLBeansLoader现在还带了一顶@Deprecated的帽子了呢
 */
@Deprecated
public class XMLBeansLoader {
    public List<Class<BeanFactoryPostProcessor>> loadStarterClasses(String directoryPath) throws DocumentException, ClassNotFoundException {
        List<Class<BeanFactoryPostProcessor>> otherStarters =new ArrayList<>();
        SAXReader reader = new SAXReader();
        URL dirURL = getClass().getClassLoader().getResource(directoryPath);
        if (dirURL != null) {
            File dir = new File(dirURL.getPath());
            File[] files = dir.listFiles((d, name) -> name.endsWith(".xml"));
            if (files != null) {
                for (File file : files) {
                    Document document = reader.read(file);
                    Element root = document.getRootElement();
                    Element autumnStarters = root.element("AutumnStarters");
                    if (autumnStarters != null) {
                        Iterator<Element> it = autumnStarters.elementIterator("bean");
                        while (it.hasNext()) {
                            Element beanElement = it.next();
                            String className = beanElement.attributeValue("class");
                            try{
                                Class<?> clazz = Class.forName(className);
                                if (BeanFactoryPostProcessor.class.isAssignableFrom(clazz)) {
                                    otherStarters.add((Class<BeanFactoryPostProcessor>) clazz);
                                } else {
                                    throw new RuntimeException(className + "不是一个标准的类型处理器");
                                }
                            }catch (ClassNotFoundException e) {
                                throw new ClassNotFoundException("找不到类" + className);
                            }
                        }
                    }
                }
            }
        }

        return otherStarters;
    }


}

