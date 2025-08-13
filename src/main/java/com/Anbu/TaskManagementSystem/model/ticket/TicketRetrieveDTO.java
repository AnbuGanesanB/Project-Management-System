package com.Anbu.TaskManagementSystem.model.ticket;

import com.Anbu.TaskManagementSystem.model.attachment.AttachmentDTO;
import com.Anbu.TaskManagementSystem.model.employee.MapperDtos.EmployeeDetailDto;
import com.Anbu.TaskManagementSystem.model.project.MapperDtos.ProjectDetailDto;
import com.Anbu.TaskManagementSystem.model.ticketHistory.TicketHistoryDto;
import lombok.Data;

import java.util.List;


@Data
public class TicketRetrieveDTO {


    private int id;
    private String title;
    private String description;
    private String status;
    private String type;
    private EmployeeDetailDto createdBy;
    private String createdOn;
    private String updatedOn;
    private ProjectDetailDto project;
    private EmployeeDetailDto assignee;
    private List<AttachmentDTO> attachments;
    private List<TicketHistoryDto> ticketHistory;

}
