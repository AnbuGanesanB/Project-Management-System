package com.Anbu.TaskManagementSystem.Repository;

import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.project.Project;
import com.Anbu.TaskManagementSystem.model.ticket.Ticket;
import com.Anbu.TaskManagementSystem.model.ticket.TicketStatus;
import com.Anbu.TaskManagementSystem.model.ticket.TicketType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TicketRepositoryImpl implements TicketRepositoryCustom{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Ticket> findTickets(Project project, List<TicketType> typeList, List<TicketStatus> statusList, List<Employee> assignedToList, List<Employee> createdByList) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Ticket> query = cb.createQuery(Ticket.class);
        Root<Ticket> ticket = query.from(Ticket.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(ticket.get("project"), project));

        if (typeList != null && !typeList.isEmpty()) {
            predicates.add(ticket.get("ticketType").in(typeList));
        }

        if (statusList != null && !statusList.isEmpty()) {
            predicates.add(ticket.get("ticketStatus").in(statusList));
        }

        if (createdByList != null && !createdByList.isEmpty()) {
            predicates.add(ticket.get("createdBy").in(createdByList));
        }

        if (assignedToList != null && !assignedToList.isEmpty()) {
            predicates.add(ticket.get("assignee").in(assignedToList));
        }

        query.select(ticket)
                .where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }
}
