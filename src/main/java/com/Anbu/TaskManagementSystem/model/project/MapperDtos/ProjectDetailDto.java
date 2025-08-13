package com.Anbu.TaskManagementSystem.model.project.MapperDtos;

import lombok.Data;

@Data
public abstract class ProjectDetailDto {

    private int id;
    private String acronym;
    private String Name;
    private String viewType;
}
