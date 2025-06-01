package com.Anbu.TaskManagementSystem.model.project;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class NewProjectDTO {

    @NotBlank(message = "Project name should not be blank")
    @NotEmpty(message = "Project name should not be empty")
    private String projectName;
    @NotBlank(message = "Acronym should not be blank")
    @NotEmpty(message = "Acronym should not be empty")
    private String acronym;
}
