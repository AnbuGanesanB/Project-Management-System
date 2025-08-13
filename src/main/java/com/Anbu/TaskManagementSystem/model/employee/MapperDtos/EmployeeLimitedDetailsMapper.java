package com.Anbu.TaskManagementSystem.model.employee.MapperDtos;

import com.Anbu.TaskManagementSystem.model.employee.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeLimitedDetailsMapper {

    public EmployeeDetailDto getLimitedEmployeeDetails(Employee employee){
        EmployeeLimitedDetailsDto employeeDetailDto = new EmployeeLimitedDetailsDto();

        employeeDetailDto.setId(employee.getId());
        employeeDetailDto.setEmpId(employee.getEmpId());
        employeeDetailDto.setName(employee.getUsername());
        employeeDetailDto.setRole(employee.getRole().name());

        return employeeDetailDto;
    }
}
