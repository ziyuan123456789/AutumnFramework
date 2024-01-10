package org.example.mapper;

import org.example.Bean.Car;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MyMapper;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MySelect;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ziyuan
 * @since 2024.01
 */
@MyMapper
public interface CarMapper {
    @MySelect("select * from car1 ")
    ArrayList<Car> getAllCarList();
}
