package com.Anbu.TaskManagementSystem.service;

import com.Anbu.TaskManagementSystem.Repository.TicketHistoryRepo;
import com.Anbu.TaskManagementSystem.Repository.TicketRepository;
import com.Anbu.TaskManagementSystem.exception.TicketException;
import com.Anbu.TaskManagementSystem.model.attachment.Attachment;
import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.project.Project;
import com.Anbu.TaskManagementSystem.model.ticket.*;
import com.Anbu.TaskManagementSystem.model.ticket.TicketAttribute;
import com.Anbu.TaskManagementSystem.model.ticketHistory.TicketHistory;
import com.Anbu.TaskManagementSystem.model.ticketHistory.TicketHistoryMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectService projectService;
    private final EmployeeService employeeService;
    private final TicketMapper ticketMapper;
    private final TicketHistoryRepo ticketHistoryRepo;
    private final TicketAttributeValidator ticketAttributeValidator;
    private final TicketHistoryMapper ticketHistoryMapper;
    private final AttachmentService attachmentService;
    private final TicketHistoryService historyService;


    public void createNewTicket(String projAcronym, NewTicketDTO newTicketDTO){
        Project targetProject = projectService.getProjectByAcronym(projAcronym);
        Employee currentUser = employeeService.getCurrentUser();

        checkUserAuthorisedToCreateOrUpdateTicket(currentUser,targetProject);

        LocalDateTime currentTime = LocalDateTime.now();

        Ticket ticket = new Ticket();
        ticket.setProject(targetProject);
        ticket.setTitle(newTicketDTO.getTitle());
        ticket.setDescription(newTicketDTO.getDescription());
        ticket.setTicketStatus(TicketStatus.valueOf(newTicketDTO.getTicketStatus().toUpperCase()));
        ticket.setTicketType(TicketType.valueOf(newTicketDTO.getTicketType().toUpperCase()));
        ticket.setCreatedBy(currentUser);
        ticket.setCreatedOn(currentTime);
        ticket.setUpdatedOn(currentTime);
        ticket = ticketRepository.save(ticket);

        TicketHistory ticketHistory = new TicketHistory();
        ticketHistory.setTicket(ticket);
        ticketHistory.setUpdatedBy(currentUser);
        ticketHistory.setUpdatedOn(currentTime);
        ticketHistory.setTicketAttribute(TicketAttribute.CREATE);
        //ticketHistoryRepo.save(ticketHistory);
        ticket.getHistories().add(ticketHistory);
        ticketRepository.save(ticket);
    }

    @Transactional
    public ResponseEntity<?> updateTicket(Integer ticketId, String attribute, List<MultipartFile> files, String value) {

        Ticket currentTicket = getTicketById(ticketId);
        Project targetProject = currentTicket.getProject();
        Employee currentUser = employeeService.getCurrentUser();
        LocalDateTime currentTime = LocalDateTime.now();

        checkUserAuthorisedToCreateOrUpdateTicket(currentUser,targetProject);

        List<TicketHistory> historiesToSave = new ArrayList<>();

        switch (attribute.toLowerCase()) {
            case "status" -> {
                ticketAttributeValidator.validateValue(TicketAttribute.STATUS,value);
                if (currentTicket.getTicketStatus() == TicketStatus.valueOf(value.toUpperCase())) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                }

                historiesToSave.add(historyService.createHistory(currentTicket,currentUser,currentTime,TicketAttribute.STATUS,currentTicket.getTicketStatus().name(),value.toUpperCase(),null,null));

                currentTicket.setTicketStatus(TicketStatus.valueOf(value.toUpperCase()));
            }
            case "type" -> {
                ticketAttributeValidator.validateValue(TicketAttribute.TYPE,value);
                if (currentTicket.getTicketType() == TicketType.valueOf(value.toUpperCase())) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                }

                historiesToSave.add(historyService.createHistory(currentTicket,currentUser,currentTime,TicketAttribute.TYPE,currentTicket.getTicketType().name(),value.toUpperCase(),null,null));

                currentTicket.setTicketType(TicketType.valueOf(value.toUpperCase()));
            }
            case "comment" -> {
                ticketAttributeValidator.validateValue(TicketAttribute.COMMENT,value);

                historiesToSave.add(historyService.createHistory(currentTicket,currentUser,currentTime,TicketAttribute.COMMENT,null,value,null,null));
            }
            case "title" -> {
                ticketAttributeValidator.validateValue(TicketAttribute.TITLE,value);
                if (currentTicket.getTitle().equalsIgnoreCase(value)) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                }

                historiesToSave.add(historyService.createHistory(currentTicket,currentUser,currentTime,TicketAttribute.TITLE,currentTicket.getTitle(),value,null,null));

                currentTicket.setTitle(value);
            }
            case "description" -> {
                ticketAttributeValidator.validateValue(TicketAttribute.DESCRIPTION,value);
                if (currentTicket.getDescription().equalsIgnoreCase(value)) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                }

                historiesToSave.add(historyService.createHistory(currentTicket,currentUser,currentTime,TicketAttribute.DESCRIPTION,currentTicket.getDescription(),value,null,null));

                currentTicket.setDescription(value);
            }
            case "assignee" -> {
                ticketAttributeValidator.validateValue(TicketAttribute.ASSIGNEE,value);

                Employee existingAssignee = currentTicket.getAssignee();
                Employee currentAssignee = value != null ? employeeService.getEmployeeByEmpId(value) : null;

                if (currentAssignee == existingAssignee) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

                } else if (currentAssignee != null) {
                    checkUserAuthorisedToCreateOrUpdateTicket(currentUser,targetProject);
                    historiesToSave.add(historyService.createHistory(currentTicket,currentUser,currentTime,TicketAttribute.ASSIGNEE,null,null,currentAssignee,null));
                    currentTicket.setAssignee(currentAssignee);
                } else {
                    historiesToSave.add(historyService.createHistory(currentTicket,currentUser,currentTime,TicketAttribute.ASSIGNEE,null,null,null,null));
                    currentTicket.setAssignee(null);
                }
            }
            case "file" -> {
                if(files == null || files.isEmpty())
                    throw new TicketException.NotValidInputException("File not selected");

                for(MultipartFile file: files){
                    Attachment attachment = attachmentService.saveFiles(currentTicket,file);
                    historiesToSave.add(historyService.createHistory(currentTicket,currentUser,currentTime,TicketAttribute.FILE,null,null,null,attachment));
                }
            }
            default -> throw new TicketException.NotValidInputException("Provide valid Attribute");
        }
        historiesToSave = ticketHistoryRepo.saveAll(historiesToSave);

        currentTicket.getHistories().addAll(historiesToSave);
        currentTicket.setUpdatedOn(currentTime);
        ticketRepository.save(currentTicket);

        return ResponseEntity.ok(historiesToSave.stream()
                .map(ticketHistoryMapper::retrieveTicketHistory)
                .toList());
    }

    public Ticket getTicketById(Integer ticketId){
        return ticketRepository.findById(ticketId).orElseThrow(()->new RuntimeException("Ticket not found"));
    }

    @Transactional
    public void deleteTicket(Integer ticketId){
        Ticket ticket = getTicketById(ticketId);
        Project targetProject = ticket.getProject();
        Employee currentUser = employeeService.getCurrentUser();

        checkUserAuthorisedToDeleteTicket(currentUser,targetProject);
        ticketRepository.delete(ticket);
    }

    public List<TicketRetrieveDTO> getAllTickets(String projAcronym, String types, String statuses, String createdBy, String assignee) {

        Project project = (projAcronym != null && !projAcronym.isEmpty()) ? projectService.getProjectByAcronym(projAcronym) : null;
        List<String> typeList = (types != null && !types.isEmpty()) ? Arrays.asList(types.split(",")) : null;
        List<String> statusList = (statuses != null && !statuses.isEmpty()) ? Arrays.asList(statuses.split(",")) : null;
        Employee createdById = (createdBy != null && !createdBy.isEmpty()) ? employeeService.getEmployeeByEmpId(createdBy) : null;
        Employee assigneeId = (assignee != null && !assignee.isEmpty()) ? employeeService.getEmployeeByEmpId(assignee) : null;

        return ticketRepository.findByFilters(typeList,statusList,project,assigneeId,createdById)
                .stream().map(ticketMapper::getTicket).collect(Collectors.toList());
    }

    void checkUserAuthorisedToCreateOrUpdateTicket(Employee currentUser, Project targetProject){
        if(!(targetProject.getProjectAdmins().contains(currentUser)) && !(targetProject.getMembers().contains(currentUser))){
            throw new TicketException.UserNotAuthorisedException("User must be Participant on this project");
        }
    }

    void checkUserAuthorisedToDeleteTicket(Employee currentUser, Project targetProject){
        if(!(targetProject.getProjectAdmins().contains(currentUser))){
            throw new TicketException.UserNotAuthorisedException("User must be Project Admin");
        }
    }


}
