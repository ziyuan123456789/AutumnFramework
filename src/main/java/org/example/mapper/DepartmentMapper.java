package org.example.mapper;

import org.example.Bean.Department;
import org.example.Bean.User;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MyMapper;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MySelect;

import java.util.List;

/**
 * @author ziyuan
 * @since 2023.10
 */
@MyMapper
public interface DepartmentMapper {
    @MySelect("select * from Department limit 1")
    Department getFirstDepartment();
    @MySelect("select * from Department")
    List<Department> getAllDepartment();
    @MySelect("select username,password from user where username='wz' and password='123'")
    User login(String username, String password);
}
