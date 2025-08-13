package com.Anbu.TaskManagementSystem.config;

import com.Anbu.TaskManagementSystem.Repository.EmployeeRepo;
import com.Anbu.TaskManagementSystem.controller.EmployeeController;
import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.employee.EmployeeCreationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AppStartUpRunner implements CommandLineRunner {

    @Autowired
    private EmployeeController employeeController;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Override
    public void run(String... args) throws Exception {

        createEmployee("3001","Admin_1","ADMIN","admin@abc.com");
        createEmployee("3002","Manager_1","MANAGER","manager@abc.com");
        createEmployee("3003","User_1","USER","user@abc.com");
    }

    void createEmployee(String empId, String userName, String role, String email) {

        Optional<Employee> adminEmployee = employeeRepo.findByEmpId(empId);
        if(adminEmployee.isEmpty()){
            EmployeeCreationDTO employeeCreationDTO = new EmployeeCreationDTO();
            employeeCreationDTO.setEmpId(empId);
            employeeCreationDTO.setUsername(userName);
            employeeCreationDTO.setRole(role);
            employeeCreationDTO.setEmail(email);
            employeeController.createEmployee(employeeCreationDTO);
        }else System.out.println("Employee with ID:"+empId+" is available!");
    }

}
