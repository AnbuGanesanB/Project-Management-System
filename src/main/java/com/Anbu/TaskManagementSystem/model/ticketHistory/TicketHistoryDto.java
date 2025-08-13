package com.Anbu.TaskManagementSystem.model.ticketHistory;

import com.Anbu.TaskManagementSystem.model.attachment.AttachmentDTO;

import com.Anbu.TaskManagementSystem.model.employee.MapperDtos.EmployeeDetailDto;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketHistoryDto {

    private int ticketId;
    private int historyId;
    private String ticketAttribute;
    private EmployeeDetailDto updatedBy;
    private LocalDateTime updatedOn;
    private String oldValue;
    private String newValue;
    private EmployeeDetailDto assignee;
    private AttachmentDTO attachment;
}
