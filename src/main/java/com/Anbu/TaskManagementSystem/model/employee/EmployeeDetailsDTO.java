package com.Anbu.TaskManagementSystem.model.employee;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeDetailsDTO {

    private int id;
    private String empId;
    private String name;
    private String email;
    private String role;
    private String status;
    private List<String> projectsManaging;
    private List<String> projectsWorking;
}
