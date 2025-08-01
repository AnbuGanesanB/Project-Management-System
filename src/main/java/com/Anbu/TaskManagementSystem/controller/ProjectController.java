package com.Anbu.TaskManagementSystem.controller;

import com.Anbu.TaskManagementSystem.config.JwtAuthFilter;
import com.Anbu.TaskManagementSystem.exception.ProjectException;
import com.Anbu.TaskManagementSystem.model.attachment.AttachmentDTO;
import com.Anbu.TaskManagementSystem.model.employee.EmployeeDetailsDTO;
import com.Anbu.TaskManagementSystem.model.project.NewProjectDTO;
import com.Anbu.TaskManagementSystem.model.project.Project;
import com.Anbu.TaskManagementSystem.model.project.ProjectDetailsDto;
import com.Anbu.TaskManagementSystem.model.project.ProjectMapper;
import com.Anbu.TaskManagementSystem.model.ticket.TicketRetrieveDTO;
import com.Anbu.TaskManagementSystem.service.ProjectService;
import com.Anbu.TaskManagementSystem.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class ProjectController {

    private final ProjectService projectService;
    private final TicketService ticketService;
    private final ProjectMapper projectMapper;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/projects")
    public ProjectDetailsDto addNewProject(@RequestBody @Valid NewProjectDTO newProjectDTO) {
        Project project = projectService.createNewProject(newProjectDTO);
        return projectMapper.retrieveProjectDetails(project);
    }

    @PreAuthorize("hasAuthority('PROJECT_VIEW')")
    @GetMapping("/projects")
    public List<ProjectDetailsDto> getAllProjects(){
        return projectService.getAllProjects();
    }

    @PreAuthorize("hasAuthority('PROJECT_VIEW')")
    @GetMapping("/projects/{id}")
    public ProjectDetailsDto getProjectDetails(@PathVariable("id") int id){
        Project project = projectService.getProjectById(id);
        return projectMapper.retrieveProjectDetails(project);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/projects/{projectId}/{role}/{empId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addProjectParticipant(@PathVariable("projectId") int projectId, @PathVariable String role, @PathVariable("empId") int empId){

        if (!role.equalsIgnoreCase("Admin") && !role.equalsIgnoreCase("Member")) {
            throw new ProjectException.InvalidInputException("Invalid role: " + role);
        }
        projectService.addParticipantToProject(projectId,empId,role);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/projects/{projectId}/{role}/{empId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void removeProjectParticipant(@PathVariable("projectId") int projectId, @PathVariable String role, @PathVariable("empId") int empId){
        if (!role.equalsIgnoreCase("Admin") && !role.equalsIgnoreCase("Member")) {
            throw new ProjectException.InvalidInputException("Invalid role: " + role);
        }
        projectService.removeParticipantFromProject(projectId,empId,role);
    }

    @PreAuthorize("hasAuthority('PROJECT_VIEW')")
    @GetMapping("/projects/{projectId}/participants")
    public Map<String,List<EmployeeDetailsDTO>> getProjectStaffs(@PathVariable("projectId") int projectId){
        return projectService.getAllProjectMembers(projectId);
    }

    @PreAuthorize("hasAuthority('TICKET_VIEW')")
    @GetMapping("/projects/{projectId}/tickets")
    public List<TicketRetrieveDTO> getAllProjectTickets(@PathVariable(value = "projectId") int projectId,
                                                        @RequestParam(value = "type", required = false) String types,
                                                        @RequestParam(value = "status", required = false) String statuses,
                                                        @RequestParam(value = "createdByEmp", required = false) int createdByEmp,
                                                        @RequestParam(value = "assignedEmp", required = false) int assignedEmp){

        return ticketService.getAllTickets(projectId,types,statuses,createdByEmp,assignedEmp);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/projects/{projectId}")
    public ProjectDetailsDto editProject(@PathVariable("projectId") int projectId, @RequestBody @Valid NewProjectDTO newProjectDTO){
        Project editedProject = projectService.editProject(projectId,newProjectDTO);
        return projectMapper.retrieveProjectDetails(editedProject);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/projects/{projectId}")
    public void deleteProject(@PathVariable("projectId") int projectId){
        projectService.deleteProject(projectId);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/projects/{projectId}/attachments")
    public List<AttachmentDTO> addAttachments(@PathVariable("projectId") int projectId,
                                              @RequestParam(value = "files",required = false) List<MultipartFile> files){
        return projectService.addAttachments(projectId,files);
    }

}
