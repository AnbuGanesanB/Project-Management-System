package com.Anbu.TaskManagementSystem.service;

import com.Anbu.TaskManagementSystem.Repository.TicketRepository;
import com.Anbu.TaskManagementSystem.exception.TicketException;
import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.project.Project;
import com.Anbu.TaskManagementSystem.model.ticket.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectService projectService;
    private final EmployeeService employeeService;
    private final TicketMapper ticketMapper;


    public void createNewTicket(String projAcronym, NewTicketDTO newTicketDTO){
        Project targetProject = projectService.getProjectByAcronym(projAcronym);
        Employee currentUser = employeeService.getCurrentUser();

        if(!isUserAuthorisedToCreateOrUpdateTicket(currentUser,targetProject)){
            return;
        }
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

        ObjectMapper mainObjectMapper = new ObjectMapper();
        ArrayNode arrayNode = mainObjectMapper.createArrayNode();
        ObjectMapper childObjectMapper = new ObjectMapper();
        ObjectNode objectNode = childObjectMapper.createObjectNode();
        objectNode.put("created On",currentTime.toString());
        objectNode.put("created By",currentUser.getUsername());
        arrayNode.add(objectNode);
        ticket.setActions(arrayNode);

        ticketRepository.save(ticket);
    }

    @Transactional
    public void updateTicket(Integer ticketId, Map<String,String> changes){
        Ticket currentTicket = getTicketById(ticketId);
        String field = changes.get("field").toLowerCase();
        String currentValue = changes.get("currentValue");
        Project targetProject = currentTicket.getProject();

        Employee currentUser = employeeService.getCurrentUser();
        LocalDateTime currentTime = LocalDateTime.now();

        if(!isUserAuthorisedToCreateOrUpdateTicket(currentUser,targetProject)){
            return;
        }

        JsonNode currentjsonNode = currentTicket.getActions();
        ArrayNode currentArrayNode = (ArrayNode) currentjsonNode;
        ObjectMapper childObjectMapper = new ObjectMapper();
        ObjectNode objectNode = childObjectMapper.createObjectNode();

        objectNode.put("updated On",currentTime.toString());
        objectNode.put("updated By",currentUser.getUsername());
        objectNode.put("Action",field);

        switch (field.toLowerCase()){
            case "status":
                if(currentTicket.getTicketStatus() == TicketStatus.valueOf(currentValue.toUpperCase())){
                    throw new TicketException.NoUpdationNeededException("No update");
                }
                objectNode.put("Old",currentTicket.getTicketStatus().name());
                objectNode.put("New",currentValue);
                currentTicket.setTicketStatus(TicketStatus.valueOf(currentValue.toUpperCase()));
                break;

            case "type":
                if(currentTicket.getTicketType() == TicketType.valueOf(currentValue.toUpperCase())){
                    throw new TicketException.NoUpdationNeededException("No update");
                }
                objectNode.put("Old",currentTicket.getTicketType().name());
                objectNode.put("New",currentValue);
                currentTicket.setTicketType(TicketType.valueOf(currentValue.toUpperCase()));
                break;

            case "comment":

                ArrayNode commentArrayNode = currentTicket.getComments() == null ? new ObjectMapper().createArrayNode() : (ArrayNode)currentTicket.getComments();
                ObjectNode commentObjectNode = new ObjectMapper().createObjectNode();
                commentObjectNode.put("commented By",currentUser.getUsername());
                commentObjectNode.put("commented On",currentTime.toString());
                commentObjectNode.put("comment",currentValue);
                commentArrayNode.add(commentObjectNode);
                currentTicket.setComments(commentArrayNode);
                break;

            case "title":
                if(currentTicket.getTitle().equalsIgnoreCase(currentValue)){
                    throw new TicketException.NoUpdationNeededException("No update");
                }
                objectNode.put("Old",currentTicket.getTitle());
                objectNode.put("New",currentValue);
                currentTicket.setTitle(currentValue);
                break;

            case "description":
                if(currentTicket.getDescription().equalsIgnoreCase(currentValue)){
                    throw new TicketException.NoUpdationNeededException("No update");
                }
                objectNode.put("Old",currentTicket.getDescription());
                objectNode.put("New",currentValue);
                currentTicket.setDescription(currentValue);
                break;

            case "assignee":
                Employee existingAssignee = currentTicket.getAssignee();
                Employee currentAssignee = currentValue != null ? employeeService.getEmployeeByEmpId(currentValue) : null;
                if(currentAssignee == existingAssignee){
                    throw new TicketException.NoUpdationNeededException("No update");
                }
                else if(currentAssignee != null){
                    if(!isUserAuthorisedToCreateOrUpdateTicket(currentAssignee,targetProject)){
                        return;
                    }
                    objectNode.put("assigned","yes");
                    objectNode.put("assignee",currentAssignee.getUsername());
                    currentTicket.setAssignee(currentAssignee);
                }
                else {
                    objectNode.put("assigned","no");
                    currentTicket.setAssignee(null);
                }
                break;

            default:
                throw new RuntimeException("Ticket Not Created!!");
        }
        currentArrayNode.add(objectNode);
        currentTicket.setActions(currentArrayNode);
        currentTicket.setUpdatedOn(currentTime);
        ticketRepository.save(currentTicket);
    }

    public Ticket getTicketById(Integer ticketId){
        return ticketRepository.findById(ticketId).orElseThrow(()->new RuntimeException("Ticket not found"));
    }

    @Transactional
    public void deleteTicket(Integer ticketId){
        Ticket ticket = getTicketById(ticketId);
        Project targetProject = ticket.getProject();
        Employee currentUser = employeeService.getCurrentUser();
        if(!isUserAuthorisedToDeleteTicket(currentUser,targetProject)){
            return;
        }
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

    boolean isUserAuthorisedToCreateOrUpdateTicket(Employee currentUser, Project targetProject){
        if(!(targetProject.getProjectAdmins().contains(currentUser)) && !(targetProject.getMembers().contains(currentUser))){
            throw new TicketException.UserNotAuthorisedException("User must be Participant on this project");
        }
        return true;
    }

    boolean isUserAuthorisedToDeleteTicket(Employee currentUser, Project targetProject){
        if(!(targetProject.getProjectAdmins().contains(currentUser))){
            throw new TicketException.UserNotAuthorisedException("User must be Project Admin");
        }
        return true;
    }

}
