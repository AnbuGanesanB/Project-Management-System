package com.Anbu.TaskManagementSystem.model.project;

import com.Anbu.TaskManagementSystem.model.attachment.AttachmentDTO;
import com.Anbu.TaskManagementSystem.model.employee.EmployeeDetailsDTO;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDetailsDto {

    private int id;
    private String acronym;
    private String Name;
    private List<EmployeeDetailsDTO> projectAdmins;
    private List<EmployeeDetailsDTO> projectMembers;
    private List<AttachmentDTO> attachments;
}
