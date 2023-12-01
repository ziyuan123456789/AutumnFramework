package org.example.service;

import org.example.Bean.Department;
import org.example.Bean.User;

import java.util.List;


public interface TestService {
    Boolean login(String username,String password);

    List<Department> getAllDepartment();
    List<Department> getAllDepartmentService();
    String printService(String s);
    String print(String s);
    void cycle();


}
