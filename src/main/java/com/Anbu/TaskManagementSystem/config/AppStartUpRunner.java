package com.Anbu.TaskManagementSystem.config;

import com.Anbu.TaskManagementSystem.Repository.EmployeeRepo;
import com.Anbu.TaskManagementSystem.controller.EmployeeController;
import com.Anbu.TaskManagementSystem.exception.EmployeeException.*;
import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.employee.EmployeeCreationDTO;
import com.Anbu.TaskManagementSystem.service.ProjectService;
import com.Anbu.TaskManagementSystem.service.TicketService;
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

        Optional<Employee> primaryEmployee = employeeRepo.findByEmpId("1001");
        if(primaryEmployee.isEmpty()){
            EmployeeCreationDTO employeeCreationDTO = new EmployeeCreationDTO();
            employeeCreationDTO.setEmpId("1001");
            employeeCreationDTO.setUsername("Admin");
            employeeCreationDTO.setRole("ADMIN");
            employeeCreationDTO.setEmail("Admin@gmail.com");
            employeeController.createEmployee(employeeCreationDTO);
        }
    }
}
