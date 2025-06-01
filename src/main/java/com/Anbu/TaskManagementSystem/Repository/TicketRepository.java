package com.Anbu.TaskManagementSystem.Repository;

import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.project.Project;
import com.Anbu.TaskManagementSystem.model.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket,Integer> {

    @Query("SELECT t FROM Ticket t WHERE " +
            "( :type IS NULL OR t.ticketType IN :type ) AND " +
            "( :status IS NULL OR t.ticketStatus IN :status ) AND " +
            "( :project IS NULL OR t.project = :project ) AND " +
            "( :assignee IS NULL OR t.assignee = :assignee ) AND " +
            "( :createdBy IS NULL OR t.createdBy = :createdBy )")
    List<Ticket> findByFilters(
                                @Param("type") List<String> type,
                                @Param("status") List<String> status,
                                @Param("project") Project project,
                                @Param("assignee") Employee assignee,
                                @Param("createdBy") Employee createdBy);
}

