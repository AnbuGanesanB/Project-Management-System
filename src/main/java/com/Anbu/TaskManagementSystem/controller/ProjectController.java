package com.Anbu.TaskManagementSystem.controller;

import com.Anbu.TaskManagementSystem.exception.ProjectException;
import com.Anbu.TaskManagementSystem.model.attachment.AttachmentDTO;
import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.employee.MapperDtos.EmployeeDetailDto;
import com.Anbu.TaskManagementSystem.model.employee.Role;
import com.Anbu.TaskManagementSystem.model.project.MapperDtos.ProjectDetailDto;
import com.Anbu.TaskManagementSystem.model.project.NewProjectDTO;
import com.Anbu.TaskManagementSystem.model.project.Project;
import com.Anbu.TaskManagementSystem.model.ticket.TicketRetrieveDTO;
import com.Anbu.TaskManagementSystem.service.EmployeeService;
import com.Anbu.TaskManagementSystem.service.ProjectService;
import com.Anbu.TaskManagementSystem.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.Anbu.TaskManagementSystem.config.ApiConstant.API_VERSION;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSION)
public class ProjectController {

    private final ProjectService projectService;
    private final TicketService ticketService;
    private final EmployeeService employeeService;

    @PreAuthorize("hasAuthority('PROJECT_MANAGE')")
    @PostMapping("/projects")
    public ProjectDetailDto addNewProject(@RequestBody @Valid NewProjectDTO newProjectDTO) {
        Project project = projectService.createNewProject(newProjectDTO);
        return projectService.getOverallProjectDetails(project);
    }

    @PreAuthorize("hasAuthority('PROJECT_MANAGE')")
    @PutMapping("/projects/{projectId}")
    public ProjectDetailDto editProject(@PathVariable("projectId") int projectId, @RequestBody @Valid NewProjectDTO newProjectDTO){
        Project editedProject = projectService.editProject(projectId,newProjectDTO);
        return projectService.getOverallProjectDetails(editedProject);
    }

    @PreAuthorize("hasAuthority('PROJECT_MANAGE')")
    @PostMapping("/projects/{projectId}/{role}/{empId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addProjectParticipant(@PathVariable("projectId") int projectId, @PathVariable String role, @PathVariable("empId") int empId){

        if (!role.equalsIgnoreCase("Admin") && !role.equalsIgnoreCase("Member")) {
            throw new ProjectException.InvalidInputException("Invalid role: " + role);
        }
        projectService.addParticipantToProject(projectId,empId,role);
    }

    @PreAuthorize("hasAuthority('PROJECT_MANAGE')")
    @DeleteMapping("/projects/{projectId}/{role}/{empId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void removeProjectParticipant(@PathVariable("projectId") int projectId, @PathVariable String role, @PathVariable("empId") int empId){
        if (!role.equalsIgnoreCase("Admin") && !role.equalsIgnoreCase("Member")) {
            throw new ProjectException.InvalidInputException("Invalid role: " + role);
        }
        projectService.removeParticipantFromProject(projectId,empId,role);
    }

    @PreAuthorize("hasAuthority('PROJECT_MANAGE')")
    @PostMapping("/projects/{projectId}/attachments")
    public List<AttachmentDTO> addAttachments(@PathVariable("projectId") int projectId,
                                              @RequestParam(value = "files",required = false) List<MultipartFile> files){
        return projectService.addAttachments(projectId,files);
    }

    @PreAuthorize("hasAuthority('PROJECT_VIEW')")
    @GetMapping("/projects/{projectId}")
    public ProjectDetailDto getProjectDetails(@PathVariable("projectId") int projectId){
        Project project = projectService.getProjectById(projectId);

        if(projectService.checkIsUserAllowedToViewProject(project,employeeService.getCurrentUser())){
            return projectService.getOverallProjectDetails(project);
        }else {
            throw new ProjectException.EmployeeNotSuitableException("User is not participant of the project");
        }
    }

    @PreAuthorize("hasAuthority('PROJECT_VIEW')")
    @GetMapping("/projects/{projectId}/participants")
    public Map<String,List<EmployeeDetailDto>> getProjectStaffs(@PathVariable("projectId") int projectId){
        Project project = projectService.getProjectById(projectId);

        if(projectService.checkIsUserAllowedToViewProject(project,employeeService.getCurrentUser())){
            return projectService.getProjectParticipants(project);
        }else {
            throw new ProjectException.EmployeeNotSuitableException("User is not participant of the project");
        }
    }

    @PreAuthorize("hasAuthority('PROJECT_VIEW')")
    @GetMapping("/projects")
    public List<ProjectDetailDto> getAllProjects(){

        Employee currentUser = employeeService.getCurrentUser();
        List<Project> applicableProjects = null;

        if(currentUser.getRole()== Role.USER || currentUser.getRole()== Role.MANAGER){
            applicableProjects = employeeService.getParticipatingProjects(currentUser);
        }else {
            applicableProjects = projectService.getAllProjects();
        }
        return applicableProjects.stream().map(projectService::getOverallProjectDetails).collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('PROJECT_DELETE')")
    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable("projectId") int projectId){
        return projectService.deleteProject(projectId);
    }

    @PreAuthorize("hasAuthority('TICKET_VIEW')")
    @GetMapping("/projects/{projectId}/tickets")
    public List<TicketRetrieveDTO> getAllProjectTickets(@PathVariable(value = "projectId") int projectId,
                                                        @RequestParam(value = "type", required = false) List<String> types,
                                                        @RequestParam(value = "status", required = false) List<String> statuses,
                                                        @RequestParam(value = "createdByEmp", required = false) List<Integer> createdByEmps,
                                                        @RequestParam(value = "assignedEmp", required = false) List<Integer> assignedEmps){

        return ticketService.getAllTickets(projectId,types,statuses,createdByEmps,assignedEmps);
    }
}
