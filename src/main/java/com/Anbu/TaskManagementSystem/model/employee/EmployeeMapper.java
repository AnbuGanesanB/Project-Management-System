package com.Anbu.TaskManagementSystem.model.employee;

import com.Anbu.TaskManagementSystem.model.project.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EmployeeMapper {

    public Employee createEmployeeFromDto(EmployeeCreationDTO employeeCreationDTO){
        Employee employee = new Employee();
        employee.setUsername(employeeCreationDTO.getUsername());
        employee.setPassword(employeeCreationDTO.getUsername());
        employee.setRole(Role.valueOf(employeeCreationDTO.getRole().toUpperCase()));
        employee.setEmpId(employeeCreationDTO.getEmpId());
        employee.setEmpStatus(EmploymentStatus.ACTIVE);
        employee.setEmail(employeeCreationDTO.getEmail());
        return employee;
    }

    public EmployeeDetailsDTO getIndividualEmployeeDetails(Employee employee){
        EmployeeDetailsDTO employeeDetailsDTO = new EmployeeDetailsDTO();
        employeeDetailsDTO.setEmpId(employee.getEmpId());
        employeeDetailsDTO.setName(employee.getUsername());
        employeeDetailsDTO.setEmail(employee.getEmail());
        employeeDetailsDTO.setRole(employee.getRole().name());
        employeeDetailsDTO.setStatus(employee.getEmpStatus().name());
        employeeDetailsDTO.setProjectsManaging(employee.getProjectsManaging().stream().map(Project::getProjectName).collect(Collectors.toList()));
        employeeDetailsDTO.setProjectsWorking(employee.getProjectsWorking().stream().map(Project::getProjectName).collect(Collectors.toList()));

        return employeeDetailsDTO;
    }
}
