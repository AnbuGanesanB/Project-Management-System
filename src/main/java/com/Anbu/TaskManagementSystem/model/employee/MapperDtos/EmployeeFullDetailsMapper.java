package com.Anbu.TaskManagementSystem.model.employee.MapperDtos;

import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.project.MapperDtos.ProjectLimitedDetailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmployeeFullDetailsMapper {

    private final ProjectLimitedDetailMapper projectLimitedDetailMapper;

    public EmployeeDetailDto getEmployeeFullDetails(Employee employee){
        EmpFullDetailsDto employeeDetailDto = new EmpFullDetailsDto();

        employeeDetailDto.setId(employee.getId());
        employeeDetailDto.setEmpId(employee.getEmpId());
        employeeDetailDto.setName(employee.getUsername());
        employeeDetailDto.setEmail(employee.getEmail());
        employeeDetailDto.setRole(employee.getRole().name());
        employeeDetailDto.setStatus(employee.getEmpStatus().name());

        employeeDetailDto.setProjectsManaging(employee.getProjectsManaging().stream().map(projectLimitedDetailMapper::getProjectDetails).collect(Collectors.toList()));
        employeeDetailDto.setProjectsWorking(employee.getProjectsWorking().stream().map(projectLimitedDetailMapper::getProjectDetails).collect(Collectors.toList()));

        return employeeDetailDto;
    }
}
