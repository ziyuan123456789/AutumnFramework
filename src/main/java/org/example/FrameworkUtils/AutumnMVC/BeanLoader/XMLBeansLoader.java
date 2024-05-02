package org.example.FrameworkUtils.AutumnMVC.BeanLoader;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.example.FrameworkUtils.AutumnMVC.Ioc.AutumnStarterRegisterer;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author ziyuan
 * @since 2024.05
 */

public class XMLBeansLoader {
    public List<Class<AutumnStarterRegisterer>> loadStarterClasses( String directoryPath) throws DocumentException, ClassNotFoundException {
        List<Class<AutumnStarterRegisterer>> otherStarters =new ArrayList<>();
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
                                if (AutumnStarterRegisterer.class.isAssignableFrom(clazz)) {
                                    otherStarters.add((Class<AutumnStarterRegisterer>) clazz);
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

