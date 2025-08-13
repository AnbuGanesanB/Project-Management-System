package com.Anbu.TaskManagementSystem.model.project.MapperDtos;

import com.Anbu.TaskManagementSystem.model.project.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectLimitedDetailMapper {

    public ProjectDetailDto getProjectDetails(Project project){

        ProjectLimitedDetailDto projectDto = new ProjectLimitedDetailDto();

        projectDto.setId(project.getId());
        projectDto.setAcronym(project.getAcronym());
        projectDto.setName(project.getProjectName());
        projectDto.setViewType("limited");

        return projectDto;
    }
}
