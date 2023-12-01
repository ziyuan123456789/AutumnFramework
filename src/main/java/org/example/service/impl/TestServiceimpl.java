package org.example.service.impl;


import org.example.Bean.User;
import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyService;
import org.example.Bean.Department;
import org.example.mapper.DepartmentMapper;
import org.example.mapper.UserMapper;
import org.example.service.CycleService;
import org.example.service.Test2Service;
import org.example.service.TestService;

import java.util.List;

/**
 * @author ziyuan
 * @since 2023.10
 */
@MyService
public class TestServiceimpl implements TestService {
    @MyAutoWired
    DepartmentMapper departmentMapper;
    @MyAutoWired
    UserMapper userMapper;

    @MyAutoWired
    Test2Service test2Service;
    @MyAutoWired
    CycleService cycleService;


    @Override
    public Boolean login(String username, String password) {
        return departmentMapper.login(username, password) != null;
    }

    @Override
    public List<Department> getAllDepartment() {
        return departmentMapper.getAllDepartment();
    }

    @Override
    public List<Department> getAllDepartmentService() {
        return test2Service.getAllDepartment();
    }

    @Override
    public String printService(String s) {
        return test2Service.print("====");
    }

    @Override
    public String print(String s) {
        System.out.println(s);
        return  s;
    }

    @Override
    public void cycle() {
        System.out.println("循环依赖");
    }


}
