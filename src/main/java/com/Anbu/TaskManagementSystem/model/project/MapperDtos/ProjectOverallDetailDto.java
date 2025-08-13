package com.Anbu.TaskManagementSystem.model.project.MapperDtos;

import com.Anbu.TaskManagementSystem.model.attachment.AttachmentDTO;
import com.Anbu.TaskManagementSystem.model.employee.MapperDtos.EmployeeDetailDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectOverallDetailDto extends ProjectDetailDto {

    private List<EmployeeDetailDto> projectAdmins;
    private List<EmployeeDetailDto> projectMembers;
    private List<AttachmentDTO> attachments;
}
