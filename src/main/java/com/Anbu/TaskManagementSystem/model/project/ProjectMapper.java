package com.Anbu.TaskManagementSystem.model.project;

import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public Project createProjectFromDto(NewProjectDTO newProjectDTO){
        Project project = new Project();
        project.setProjectName(newProjectDTO.getProjectName());
        project.setAcronym(newProjectDTO.getAcronym());
        return project;
    }
}
