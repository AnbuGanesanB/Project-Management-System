package com.Anbu.TaskManagementSystem.service;

import com.Anbu.TaskManagementSystem.model.attachment.Attachment;
import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.ticket.Ticket;
import com.Anbu.TaskManagementSystem.model.ticket.TicketAttribute;
import com.Anbu.TaskManagementSystem.model.ticketHistory.TicketHistory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TicketHistoryService {

    public TicketHistory createHistory(Ticket ticket, Employee creator, LocalDateTime time,
                                        TicketAttribute attribute,
                                        String oldValue, String newValue,
                                        Employee assignee, Attachment attachment) {
        TicketHistory history = new TicketHistory();
        history.setTicket(ticket);
        history.setUpdatedBy(creator);
        history.setUpdatedOn(time);
        history.setTicketAttribute(attribute);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setAssignee(assignee);
        history.setAttachment(attachment);
        return history;
    }

}
