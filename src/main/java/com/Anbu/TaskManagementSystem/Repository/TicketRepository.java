package com.Anbu.TaskManagementSystem.Repository;

import com.Anbu.TaskManagementSystem.model.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket,Integer>, TicketRepositoryCustom {

}

