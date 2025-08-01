package com.Anbu.TaskManagementSystem.model.project;

import com.Anbu.TaskManagementSystem.model.attachment.AttachmentMapper;
import com.Anbu.TaskManagementSystem.model.employee.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectMapper {

    private final EmployeeMapper employeeMapper;
    private final AttachmentMapper attachmentMapper;

    public Project createProjectFromDto(NewProjectDTO newProjectDTO){
        Project project = new Project();
        project.setProjectName(newProjectDTO.getProjectName());
        project.setAcronym(newProjectDTO.getAcronym());
        return project;
    }


    public ProjectDetailsDto retrieveProjectDetails(Project project){
        ProjectDetailsDto projectDetailsDto = new ProjectDetailsDto();

        projectDetailsDto.setId(project.getId());
        projectDetailsDto.setName(project.getProjectName());
        projectDetailsDto.setAcronym(project.getAcronym());

        projectDetailsDto.setProjectAdmins(project.getProjectAdmins()
                .stream()
                .map(employeeMapper::getIndividualEmployeeDetails)
                .collect(Collectors.toList()));

        projectDetailsDto.setProjectMembers(project.getMembers()
                .stream()
                .map(employeeMapper::getIndividualEmployeeDetails)
                .collect(Collectors.toList()));

        projectDetailsDto.setAttachments(project.getAttachment()
                .stream()
                .map(attachmentMapper::provideAttachmentDto)
                .collect(Collectors.toList()));

        return projectDetailsDto;
    }
}
