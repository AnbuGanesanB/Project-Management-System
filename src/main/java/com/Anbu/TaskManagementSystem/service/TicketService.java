package com.Anbu.TaskManagementSystem.service;

import com.Anbu.TaskManagementSystem.Repository.TicketRepository;
import com.Anbu.TaskManagementSystem.exception.TicketException;
import com.Anbu.TaskManagementSystem.model.attachment.Attachment;
import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.employee.Role;
import com.Anbu.TaskManagementSystem.model.project.Project;
import com.Anbu.TaskManagementSystem.model.ticket.*;
import com.Anbu.TaskManagementSystem.model.ticket.TicketAttribute;
import com.Anbu.TaskManagementSystem.model.ticketHistory.TicketHistory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectService projectService;
    private final EmployeeService employeeService;
    private final TicketMapper ticketMapper;
    private final TicketAttributeValidator ticketAttributeValidator;
    private final AttachmentService attachmentService;
    private final TicketHistoryService historyService;

    @Transactional
    public Ticket createNewTicket(int projectId, NewTicketDTO newTicketDTO){
        Project targetProject = projectService.getProjectById(projectId);
        Employee currentUser = employeeService.getCurrentUser();

        checkUserAuthorised(currentUser,targetProject);

        LocalDateTime currentTime = LocalDateTime.now();

        String title = newTicketDTO.getTitle();
        String description = newTicketDTO.getDescription();
        String ticketStatus = newTicketDTO.getTicketStatus().toUpperCase();
        String ticketType = newTicketDTO.getTicketType().toUpperCase();

        ticketAttributeValidator.validateValue(TicketAttribute.TITLE, title);
        ticketAttributeValidator.validateValue(TicketAttribute.DESCRIPTION, description);
        ticketAttributeValidator.validateValue(TicketAttribute.STATUS, ticketStatus);
        ticketAttributeValidator.validateValue(TicketAttribute.TYPE, ticketType);

        Ticket ticket = new Ticket();
        ticket.setProject(targetProject);
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setTicketStatus(TicketStatus.valueOf(ticketStatus));
        ticket.setTicketType(TicketType.valueOf(ticketType));
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
        return ticketRepository.save(ticket);
    }

    @Transactional
    public ResponseEntity<?> updateTicket(Integer ticketId, String attribute, List<MultipartFile> files, String value) {

        Ticket currentTicket = getTicketById(ticketId);
        Project targetProject = currentTicket.getProject();
        Employee currentUser = employeeService.getCurrentUser();
        LocalDateTime currentTime = LocalDateTime.now();

        checkUserAuthorised(currentUser,targetProject);

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

                Employee existingAssignee = currentTicket.getAssignee();
                Employee newAssignee = null;

                if(value != null){
                    try{
                        int empId = Integer.parseInt(value);
                        newAssignee = employeeService.getEmployeeById(empId);
                    }catch (NumberFormatException e){
                        throw new TicketException.NotValidInputException("Provide valid Assignee ID");
                    }
                }

                if (newAssignee == existingAssignee) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

                } else if (newAssignee != null) {
                    checkUserAuthorised(newAssignee,targetProject);
                    historiesToSave.add(historyService.createHistory(currentTicket,currentUser,currentTime,TicketAttribute.ASSIGNEE,null,null,newAssignee,null));
                    currentTicket.setAssignee(newAssignee);
                } else {
                    historiesToSave.add(historyService.createHistory(currentTicket,currentUser,currentTime,TicketAttribute.ASSIGNEE,null,null,null,null));
                    currentTicket.setAssignee(null);
                }
            }
            case "file" -> {
                if(files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty))
                    throw new TicketException.NotValidInputException("File not selected");

                for(MultipartFile file: files){
                    Attachment attachment = attachmentService.saveFiles(currentTicket,file);
                    historiesToSave.add(historyService.createHistory(currentTicket,currentUser,currentTime,TicketAttribute.FILE,null,null,null,attachment));
                }
            }
            default -> throw new TicketException.NotValidInputException("Provide valid Attribute");
        }

        currentTicket.getHistories().addAll(historiesToSave);
        currentTicket.setUpdatedOn(currentTime);
        currentTicket = ticketRepository.save(currentTicket);

        return ResponseEntity.ok(ticketMapper.getTicket(currentTicket));
    }

    public Ticket getTicketById(Integer ticketId){
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(()->new RuntimeException("Ticket not found"));
        checkUserAuthorised(employeeService.getCurrentUser(),ticket.getProject());
        return ticket;
    }

    @Transactional
    public ResponseEntity<Void> deleteTicket(Integer ticketId){
        if(ticketRepository.findById(ticketId).isEmpty()) return ResponseEntity.notFound().build();
        else{
            Ticket ticket = getTicketById(ticketId);
            Project targetProject = ticket.getProject();
            Employee currentUser = employeeService.getCurrentUser();
            checkUserAuthorised(currentUser,targetProject);

            ticketRepository.delete(ticket);
            return ResponseEntity.noContent().build();
        }
    }

    public List<TicketRetrieveDTO> getAllTickets(Integer projectId, List<String> types, List<String> statuses, List<Integer> createdByEmps, List<Integer> assignedEmps) {

        Project project = projectService.getProjectById(projectId);
        checkUserAuthorised(employeeService.getCurrentUser(),project);

        List<TicketType> typeList = (types != null && !types.isEmpty())
                                ? types.stream()
                                .filter(Objects::nonNull)
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .map(String::toUpperCase)
                                .peek(type -> ticketAttributeValidator.validateValue(TicketAttribute.TYPE, type))
                                .map(TicketType::valueOf)
                                .collect(Collectors.toList())
                                : null;

        List<TicketStatus> statusList = (statuses != null && !statuses.isEmpty())
                                ? statuses.stream()
                                .filter(Objects::nonNull)
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .map(String::toUpperCase)
                                .peek(status -> ticketAttributeValidator.validateValue(TicketAttribute.STATUS, status))
                                .map(TicketStatus::valueOf)
                                .collect(Collectors.toList())
                                : null;

        List<Employee> createdByEmployees = (createdByEmps != null && !createdByEmps.isEmpty())
                                ? createdByEmps.stream()
                                .filter(Objects::nonNull)
                                .map(employeeService::getEmployeeById)
                                .collect(Collectors.toList())
                                : null;

        List<Employee> assignedEmployees = (assignedEmps != null && !assignedEmps.isEmpty())
                                ? assignedEmps.stream()
                                .filter(Objects::nonNull)
                                .map(employeeService::getEmployeeById)
                                .collect(Collectors.toList())
                                : null;

        return ticketRepository.findTickets(project,typeList,statusList,assignedEmployees,createdByEmployees)
                .stream().map(ticketMapper::getTicket).collect(Collectors.toList());
    }

    @Transactional
    public void checkUserAuthorised(Employee currentUser, Project targetProject){
        if(currentUser.getRole()== Role.ADMIN) return;
        else if(!(targetProject.getProjectAdmins().contains(currentUser)) && !(targetProject.getMembers().contains(currentUser))){
            throw new TicketException.UserNotAuthorisedException("User must be Participant on this project");
        }
    }

}
