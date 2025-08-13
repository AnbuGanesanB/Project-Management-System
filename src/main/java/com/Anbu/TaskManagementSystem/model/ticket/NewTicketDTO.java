package com.Anbu.TaskManagementSystem.model.ticket;

import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.project.Project;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@NotNull
public class NewTicketDTO {

    @NotNull(message = "Title should be mandatory")
    private String title;

    @NotNull(message = "Description should be mandatory")
    private String description;

    @NotNull(message = "Any status should be mandatory")
    private String ticketStatus;

    @NotNull(message = "Any Type should be mandatory")
    private String ticketType;

}
