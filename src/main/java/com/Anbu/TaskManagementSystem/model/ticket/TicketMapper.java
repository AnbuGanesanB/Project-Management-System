package com.Anbu.TaskManagementSystem.model.ticket;

import com.Anbu.TaskManagementSystem.model.employee.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketMapper {


    public TicketRetrieveDTO getTicket(Ticket ticket){

        TicketRetrieveDTO ticketRetrieveDTO = new TicketRetrieveDTO();

        ticketRetrieveDTO.setId(ticket.getId());
        ticketRetrieveDTO.setTitle(ticket.getTitle());
        ticketRetrieveDTO.setDescription(ticket.getDescription());
        ticketRetrieveDTO.setStatus(ticket.getTicketStatus().name());
        ticketRetrieveDTO.setType(ticket.getTicketType().name());
        ticketRetrieveDTO.setCreatedBy(ticket.getCreatedBy().getUsername());
        ticketRetrieveDTO.setCreatedOn(ticket.getCreatedOn().toString());
        ticketRetrieveDTO.setUpdatedOn(ticket.getUpdatedOn().toString());

        Employee assignee = ticket.getAssignee();
        ticketRetrieveDTO.setAssignee(assignee==null ? null : assignee.getUsername());

        ticketRetrieveDTO.setProject(ticket.getProject().getProjectName());
        ticketRetrieveDTO.setHistory(ticket.getActions());
        ticketRetrieveDTO.setComments(ticket.getComments());
        return ticketRetrieveDTO;
    }
}
