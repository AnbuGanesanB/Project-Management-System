package com.Anbu.TaskManagementSystem.model.ticketHistory;

import com.Anbu.TaskManagementSystem.model.attachment.AttachmentDTO;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketHistoryDto {

    private int ticketId;
    private String ticketAttribute;
    private String updatedBy;
    private LocalDateTime updatedOn;
    private String oldValue;
    private String newValue;
    private String assignee;
    private AttachmentDTO attachment;
}
