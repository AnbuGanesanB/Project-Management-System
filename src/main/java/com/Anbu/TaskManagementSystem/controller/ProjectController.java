package com.Anbu.TaskManagementSystem.controller;

import com.Anbu.TaskManagementSystem.config.JwtAuthFilter;
import com.Anbu.TaskManagementSystem.model.project.NewProjectDTO;
import com.Anbu.TaskManagementSystem.model.project.Project;
import com.Anbu.TaskManagementSystem.model.ticket.TicketRetrieveDTO;
import com.Anbu.TaskManagementSystem.service.ProjectService;
import com.Anbu.TaskManagementSystem.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/")
public class ProjectController {

    private final JwtAuthFilter jwtAuthFilter;
    private final ProjectService projectService;
    private final TicketService ticketService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("projects/add_project")
    public String addNewProject(@RequestBody @Valid NewProjectDTO newProjectDTO) {
        try {
            projectService.createNewProject(newProjectDTO);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "Project Created Successfully";
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("projects/{acronym}/admins/{empId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String addProjectAdmin(@PathVariable("acronym") String projAcronym, @PathVariable("empId") String empId){
        projectService.addAdminToProject(projAcronym,empId);
        return "Admin Added";
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("projects/{acronym}/members/{empId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String addProjectMember(@PathVariable("acronym") String projAcronym, @PathVariable("empId") String empId){
        projectService.addMemberToProject(projAcronym,empId);
        return "Member Added";
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("projects/{acronym}/admins/{empId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String removeProjectAdmin(@PathVariable("acronym") String projAcronym, @PathVariable("empId") String empId){
        projectService.removeAdminFromProject(projAcronym,empId);
        return "Admin Removed";
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("projects/{acronym}/members/{empId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String removeProjectMember(@PathVariable("acronym") String projAcronym, @PathVariable("empId") String empId){
        projectService.removeMemberFromProject(projAcronym,empId);
        return "Member Removed";
    }

    @PreAuthorize("hasAuthority('PROJECT_VIEW')")
    @GetMapping("projects/{acronym}/all_participants")
    public Map<String, List<String>> getProjectStaffs(@PathVariable("acronym") String projAcronym){
        return projectService.getAllProjectMembers(projAcronym);
    }

    @PreAuthorize("hasAuthority('PROJECT_VIEW')")
    @GetMapping("projects")
    public List<String> getAllProjects(){
        return projectService.getAllProjects();
    }

    @PreAuthorize("hasAuthority('PROJECT_VIEW')")
    @GetMapping("projects/{acronym}/non_participants")
    public Map<String, List<String>> getProjectNonStaffs(@PathVariable("acronym") String projAcronym){
        return projectService.getProjectNonParticipants(projAcronym);
    }

    @PreAuthorize("hasAuthority('TICKET_VIEW')")
    @GetMapping("projects/{acronym}/tickets")
    public List<TicketRetrieveDTO> getAllProjectTickets(@PathVariable(value = "acronym") String projAcronym,
                                                        @RequestParam(value = "type", required = false) String types,
                                                        @RequestParam(value = "status", required = false) String statuses,
                                                        @RequestParam(value = "createdBy", required = false) String createdBy,
                                                        @RequestParam(value = "assignee", required = false) String assignee){

        return ticketService.getAllTickets(projAcronym,types,statuses,createdBy,assignee);
    }

}
