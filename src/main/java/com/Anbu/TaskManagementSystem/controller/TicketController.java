package com.Anbu.TaskManagementSystem.controller;

import com.Anbu.TaskManagementSystem.model.ticket.NewTicketDTO;
import com.Anbu.TaskManagementSystem.model.ticket.TicketMapper;
import com.Anbu.TaskManagementSystem.model.ticket.TicketRetrieveDTO;
import com.Anbu.TaskManagementSystem.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.Anbu.TaskManagementSystem.config.ApiConstant.API_VERSION;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSION)
public class TicketController {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;

    @PreAuthorize("hasAuthority('TICKET_CREATE')")
    @PostMapping("/projects/{projectId}/tickets")
    public TicketRetrieveDTO createTicket(@PathVariable("projectId")int projectId, @RequestBody @Valid NewTicketDTO newTicketDTO){
        return ticketMapper.getTicket(ticketService.createNewTicket(projectId,newTicketDTO));
    }

    @PreAuthorize("hasAuthority('TICKET_VIEW')")
    @GetMapping("/tickets/{ticketId}")
    public TicketRetrieveDTO getTicketDetails(@PathVariable("ticketId")Integer ticketId){
        return ticketMapper.getTicket(ticketService.getTicketById(ticketId));
    }

    @PreAuthorize("hasAuthority('TICKET_UPDATE')")
    @PatchMapping("/tickets/{ticketId}")
    public ResponseEntity<?> updateTicket(@PathVariable("ticketId") Integer ticketId,
                                       @RequestParam("attribute") String attribute,
                                       @RequestParam(value = "files",required = false) List<MultipartFile> files,
                                       @RequestParam(value = "value",required = false) String value){
        return ticketService.updateTicket(ticketId,attribute,files,value);
    }

    @PreAuthorize("hasAuthority('TICKET_DELETE')")
    @DeleteMapping("/tickets/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable("ticketId")Integer ticketId){
        return ticketService.deleteTicket(ticketId);
    }

}
