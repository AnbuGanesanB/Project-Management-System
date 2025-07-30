package com.Anbu.TaskManagementSystem.model.ticket;

import com.Anbu.TaskManagementSystem.model.attachment.AttachmentDTO;
import com.Anbu.TaskManagementSystem.model.ticketHistory.TicketHistoryDto;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
public class TicketRetrieveDTO {


    private int id;
    private String title;
    private String description;
    private String status;
    private String type;
    private String createdBy;
    private String createdOn;
    private String updatedOn;
    private String project;
    private String assignee;
    private List<AttachmentDTO> attachments;
    private List<TicketHistoryDto> ticketHistory;

}
