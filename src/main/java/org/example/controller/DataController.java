package org.example.controller;

import lombok.extern.slf4j.Slf4j;

import org.example.Bean.Department;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyController;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyRequestParam;
import org.example.FrameworkUtils.AutumnMVC.Annotation.Value;
import org.example.FrameworkUtils.AutumnMVC.MyContext;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyRequest;
import org.example.mapper.DepartmentMapper;
import org.example.service.Test2Service;
import org.example.service.TestService;

import java.util.List;

/**
 * @author ziyuan
 * @since 2023.10
 */
@MyController
@Slf4j
public class DataController {
    @MyAutoWired
    DepartmentMapper departmentMapper;
    @MyAutoWired
    TestService testService;

    @MyAutoWired
    Test2Service test2Service;

    MyContext myContext = MyContext.getInstance();

    @Value("url")
    String sqlUrl;
    @Value("user")
    String user;
    @Value("password")
    String password;

    @MyRequestMapping("/easytest")
    public int easytest(MyRequest myRequest) {
        Integer[] nums = {1, 2, 3, 4, 5};
        int target = 1;
        if (nums.length == 0) {
            return -1;
        }
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (target == nums[mid]) {
                return mid;
            }
            if (nums[mid] > target) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }

        }
        return -1;
    }

    @MyRequestMapping("/test")
    public String test(@MyRequestParam("str") String str, MyRequest myRequest) {
        System.out.println(myRequest.getParameters());
        System.out.println(myRequest.getUrl());
        System.out.println(myRequest.getMethod());
        System.out.println(str);
        return "test";
    }

    @MyRequestMapping("/test0")
    public String test0() {
        return myContext.getBean(DataController.class).toString();
    }

    @MyRequestMapping("/test1")
    public String test1(MyRequest myRequest) {
        return myContext.getBean(DataController.class).toString();
    }


    @MyRequestMapping("/getone")
    public Department getOne(MyRequest myRequest) {
        System.out.println(myRequest.getMethod());
        System.out.println(myRequest.getUrl());
        return departmentMapper.getFirstDepartment();
    }

    @MyRequestMapping("/getall")
    public List<Department> getAll( MyRequest requestrequestrequestrequest) {
        return departmentMapper.getAllDepartment();
    }

}
