package com.Anbu.TaskManagementSystem.model.ticket;

import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.project.Project;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class NewTicketDTO {

    private String title;

    private String description;

    private String ticketStatus;

    private String ticketType;

}
