package com.Anbu.TaskManagementSystem.controller;

import com.Anbu.TaskManagementSystem.model.ticket.NewTicketDTO;
import com.Anbu.TaskManagementSystem.model.ticket.TicketMapper;
import com.Anbu.TaskManagementSystem.model.ticket.TicketRetrieveDTO;
import com.Anbu.TaskManagementSystem.service.AttachmentService;
import com.Anbu.TaskManagementSystem.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class TicketController {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;

    @PreAuthorize("hasAuthority('TICKET_CREATE')")
    @PostMapping("/projects/{projectId}/tickets")
    public TicketRetrieveDTO createTicket(@PathVariable("projectId")int projectId, @RequestBody NewTicketDTO newTicketDTO){
        return ticketMapper.getTicket(ticketService.createNewTicket(projectId,newTicketDTO));
    }

    @PreAuthorize("hasAuthority('TICKET_VIEW')")
    @GetMapping("/tickets/{ticketId}")
    public TicketRetrieveDTO getTicketDetails(@PathVariable("ticketId")Integer ticketId){
        return ticketMapper.getTicket(ticketService.getTicketById(ticketId));
    }

    @PreAuthorize("hasAuthority('TICKET_UPDATE')")
    @PostMapping("/tickets/{ticketId}")
    public ResponseEntity<?> updateTicket(@RequestParam("ticketId") Integer ticketId,
                                       @RequestParam("attribute") String attribute,
                                       @RequestParam(value = "files",required = false) List<MultipartFile> files,
                                       @RequestParam(value = "value",required = false) String value){
        return ticketService.updateTicket(ticketId,attribute,files,value);
    }

    @PreAuthorize("hasAuthority('TICKET_VIEW')")
    @DeleteMapping("/tickets/{ticketId}")
    public void deleteTicket(@PathVariable("ticketId")Integer ticketId){
        ticketService.deleteTicket(ticketId);
    }

    @PreAuthorize("hasAuthority('TICKET_VIEW')")
    @GetMapping("/tickets")
    public List<TicketRetrieveDTO> getAllProjectTickets(@RequestParam(value = "projectId", required = false) int projectId,
                                                        @RequestParam(value = "type", required = false) String types,
                                                        @RequestParam(value = "status", required = false) String statuses,
                                                        @RequestParam(value = "createdByEmp", required = false) int createdByEmp,
                                                        @RequestParam(value = "assigneeEmp", required = false) int assigneeEmp){

        return ticketService.getAllTickets(projectId,types,statuses,createdByEmp,assigneeEmp);
    }
}
