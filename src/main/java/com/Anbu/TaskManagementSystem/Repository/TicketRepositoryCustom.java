package com.Anbu.TaskManagementSystem.Repository;

import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.project.Project;
import com.Anbu.TaskManagementSystem.model.ticket.Ticket;
import com.Anbu.TaskManagementSystem.model.ticket.TicketStatus;
import com.Anbu.TaskManagementSystem.model.ticket.TicketType;

import java.util.List;

public interface TicketRepositoryCustom {

    List<Ticket> findTickets(Project project,
                             List<TicketType> type,
                             List<TicketStatus> status,
                             List<Employee> assignee,
                             List<Employee> createdBy);
}
