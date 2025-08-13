package com.Anbu.TaskManagementSystem.model.employee.MapperDtos;

import com.Anbu.TaskManagementSystem.model.employee.MapperDtos.EmployeeDetailDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmployeeLimitedDetailsDto extends EmployeeDetailDto {

    private String role;
}
