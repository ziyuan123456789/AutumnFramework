package org.example.controller;

import lombok.extern.slf4j.Slf4j;

import org.example.Bean.Department;
import org.example.FrameworkUtils.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Annotation.MyController;
import org.example.FrameworkUtils.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.Annotation.MyRequestParam;
import org.example.FrameworkUtils.Annotation.Value;
import org.example.FrameworkUtils.Webutils.MyContext;
import org.example.FrameworkUtils.Webutils.Request;
import org.example.mapper.DepartmentMapper;
import org.example.service.Test2Service;
import org.example.service.TestService;
import java.lang.reflect.Method;
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
    public int easytest(Request request) {
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
    public String test(@MyRequestParam("str") String str,Request request) {
        System.out.println(request.getParameters());
        System.out.println(request.getUrl());
        System.out.println(request.getMethod());
        System.out.println(str);
        return "test";
    }

    @MyRequestMapping("/test0")
    public String test0() {
        return myContext.getBean(DataController.class).toString();
    }

    @MyRequestMapping("/test1")
    public String test1(Request request) {
        return myContext.getBean(DataController.class).toString();
    }


    @MyRequestMapping("/getone")
    public Department getOne(Request request) {
        System.out.println(request.getMethod());
        System.out.println(request.getUrl());
        return departmentMapper.getFirstDepartment();
    }

    @MyRequestMapping("/getall")
    public List<Department> getAll( Request requestrequestrequestrequest) {
        return departmentMapper.getAllDepartment();
    }

}
