package org.example.mapper;

import org.example.Bean.Temp;
import org.example.FrameworkUtils.Annotation.MyMapper;
import org.example.FrameworkUtils.Annotation.MySelect;

import java.util.List;

/**
 * @author wangzhiyi
 * @since 2023.10
 */
@MyMapper
public interface TestMapper {
    @MySelect("select * from Temp limit 5")
    List<Temp> selectById(Integer id);

}
