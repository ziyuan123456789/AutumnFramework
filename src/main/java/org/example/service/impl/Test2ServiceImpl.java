package org.example.service.impl;

import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyService;
import org.example.Bean.Department;
import org.example.mapper.DepartmentMapper;
import org.example.service.Test2Service;
import org.example.service.TestService;

import java.util.List;


/**
 * @author ziyuan
 * @since 2023.11
 */
@MyService
public class Test2ServiceImpl implements Test2Service {
    @MyAutoWired
    DepartmentMapper departmentMapper;

    @MyAutoWired
    TestService testService;

    public List<Department> getAllDepartment() {
        return departmentMapper.getAllDepartment();
    }

    @Override
    public String print(String s) {
        return testService.print("=====");
    }
}
