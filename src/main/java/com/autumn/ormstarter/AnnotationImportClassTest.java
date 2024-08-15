package com.autumn.ormstarter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ziyuan
 * @since 2024.07
 */
@Data
@Slf4j
public class AnnotationImportClassTest {
    static {
        log.warn("孩子们,{}这把我是真复活了", AnnotationImportClassTest.class.getSimpleName());
    }

    private String name="橘皮乌龙";
}
