package com.Anbu.TaskManagementSystem.model.employee.MapperDtos;

import com.Anbu.TaskManagementSystem.model.project.MapperDtos.ProjectDetailDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmpFullDetailsDto extends EmployeeDetailDto {

    private String role;
    private String email;
    private String status;
    private List<ProjectDetailDto> projectsManaging;
    private List<ProjectDetailDto> projectsWorking;
}
