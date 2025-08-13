package com.Anbu.TaskManagementSystem.model.ticket;

import com.Anbu.TaskManagementSystem.model.attachment.AttachmentMapper;
import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.employee.MapperDtos.EmployeeLimitedDetailsMapper;
import com.Anbu.TaskManagementSystem.model.project.MapperDtos.ProjectLimitedDetailMapper;
import com.Anbu.TaskManagementSystem.model.ticketHistory.TicketHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TicketMapper {

    private final AttachmentMapper attachmentMapper;
    private final TicketHistoryMapper ticketHistoryMapper;
    private final EmployeeLimitedDetailsMapper employeeLimitedDetailsMapper;
    private final ProjectLimitedDetailMapper projectLimitedDetailMapper;

    public TicketRetrieveDTO getTicket(Ticket ticket){

        TicketRetrieveDTO ticketRetrieveDTO = new TicketRetrieveDTO();

        ticketRetrieveDTO.setId(ticket.getId());
        ticketRetrieveDTO.setTitle(ticket.getTitle());
        ticketRetrieveDTO.setDescription(ticket.getDescription());
        ticketRetrieveDTO.setStatus(ticket.getTicketStatus().name());
        ticketRetrieveDTO.setType(ticket.getTicketType().name());
        ticketRetrieveDTO.setCreatedBy(employeeLimitedDetailsMapper.getLimitedEmployeeDetails(ticket.getCreatedBy()));
        ticketRetrieveDTO.setCreatedOn(ticket.getCreatedOn().toString());
        ticketRetrieveDTO.setUpdatedOn(ticket.getUpdatedOn().toString());

        Employee assignee = ticket.getAssignee();
        ticketRetrieveDTO.setAssignee(assignee==null ? null : employeeLimitedDetailsMapper.getLimitedEmployeeDetails(assignee));

        ticketRetrieveDTO.setProject(projectLimitedDetailMapper.getProjectDetails(ticket.getProject()));

        ticketRetrieveDTO.setAttachments(ticket.getAttachment()
                .stream()
                .map(attachmentMapper::provideAttachmentDto)
                .collect(Collectors.toList()));

        ticketRetrieveDTO.setTicketHistory(ticket.getHistories()
                .stream()
                .map(ticketHistoryMapper::retrieveTicketHistory)
                .collect(Collectors.toList()));

        return ticketRetrieveDTO;
    }
}
