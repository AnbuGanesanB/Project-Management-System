package com.Anbu.TaskManagementSystem.model.project.MapperDtos;

import com.Anbu.TaskManagementSystem.model.attachment.AttachmentMapper;
import com.Anbu.TaskManagementSystem.model.employee.MapperDtos.EmployeeLimitedDetailsMapper;
import com.Anbu.TaskManagementSystem.model.project.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectOverallDetailMapper {

    private final EmployeeLimitedDetailsMapper employeeLimitedDetailsMapper;
    private final AttachmentMapper attachmentMapper;

    public ProjectDetailDto getProjectDetails(Project project){

        ProjectOverallDetailDto projectDto = new ProjectOverallDetailDto();

        projectDto.setId(project.getId());
        projectDto.setName(project.getProjectName());
        projectDto.setAcronym(project.getAcronym());
        projectDto.setViewType("overall");

        projectDto.setProjectAdmins(project.getProjectAdmins()
                .stream()
                .map(employeeLimitedDetailsMapper::getLimitedEmployeeDetails)
                .collect(Collectors.toList()));

        projectDto.setProjectMembers(project.getMembers()
                .stream()
                .map(employeeLimitedDetailsMapper::getLimitedEmployeeDetails)
                .collect(Collectors.toList()));

        projectDto.setAttachments(project.getAttachment()
                .stream()
                .map(attachmentMapper::provideAttachmentDto)
                .collect(Collectors.toList()));

        return projectDto;
    }
}
