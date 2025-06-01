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
@RequestMapping("/v1/")
public class TicketController {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final AttachmentService attachmentService;

    @PreAuthorize("hasAuthority('TICKET_CREATE')")
    @PostMapping("/projects/{projAcronym}/add_ticket")
    public String createTicket(@PathVariable("projAcronym")String projAcronym, @RequestBody NewTicketDTO newTicketDTO){
        ticketService.createNewTicket(projAcronym,newTicketDTO);
        return "Ticket created";
    }

    @PreAuthorize("hasAuthority('TICKET_UPDATE')")
    @PostMapping("/projects/tickets/{ticketId}")
    public String updateTicket(@PathVariable("ticketId")Integer ticketId, @RequestBody Map<String,String> changes){
        ticketService.updateTicket(ticketId,changes);
        return "Ticket Updated";
    }

    @PreAuthorize("hasAuthority('TICKET_UPDATE')")
    @PostMapping("/projects/tickets/{ticketId}/upload")
    public void uploadAttachment(@PathVariable("ticketId")Integer ticketId, @RequestParam("file[]") List<MultipartFile> files){
        attachmentService.uploadFiles(ticketId, files);
    }

    @PreAuthorize("hasAuthority('TICKET_VIEW')")
    @GetMapping("/projects/tickets/{ticketId}")
    public TicketRetrieveDTO getTicketDetails(@PathVariable("ticketId")Integer ticketId){
        return ticketMapper.getTicket(ticketService.getTicketById(ticketId));
    }

    @PreAuthorize("hasAuthority('TICKET_VIEW')")
    @DeleteMapping("tickets/{ticketId}")
    public void deleteTicket(@PathVariable("ticketId")Integer ticketId){
        ticketService.deleteTicket(ticketId);
    }

    @PreAuthorize("hasAuthority('TICKET_VIEW')")
    @GetMapping("tickets")
    public List<TicketRetrieveDTO> getAllProjectTickets(@PathVariable(value = "acronym", required = false) String projAcronym,
                                                        @RequestParam(value = "type", required = false) String types,
                                                        @RequestParam(value = "status", required = false) String statuses,
                                                        @RequestParam(value = "createdBy", required = false) String createdBy,
                                                        @RequestParam(value = "assignee", required = false) String assignee){

        return ticketService.getAllTickets(projAcronym,types,statuses,createdBy,assignee);
    }
}
