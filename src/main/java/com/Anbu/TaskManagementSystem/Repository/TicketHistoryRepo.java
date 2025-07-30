package com.Anbu.TaskManagementSystem.Repository;

import com.Anbu.TaskManagementSystem.model.ticketHistory.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketHistoryRepo extends JpaRepository<TicketHistory, Integer> {

}
