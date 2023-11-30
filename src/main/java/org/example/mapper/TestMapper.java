package org.example.mapper;

import org.example.Bean.Temp;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MyMapper;
import org.example.FrameworkUtils.Annotation.MyParam;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MySelect;

import java.util.List;

/**
 * @author ziyuan
 * @since 2023.10
 */
@MyMapper
public interface TestMapper {
    @MySelect("select * from Temp limit 5")
    List<Temp> selectById(Integer id);

    @MySelect("select * from Temp limit #{id}")
    Temp selectById1(@MyParam("id") Integer id);

}
