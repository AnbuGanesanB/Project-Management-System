package com.Anbu.TaskManagementSystem.service;

import com.Anbu.TaskManagementSystem.Repository.EmployeeRepo;
import com.Anbu.TaskManagementSystem.Repository.ProjectRepository;
import com.Anbu.TaskManagementSystem.exception.ProjectException;
import com.Anbu.TaskManagementSystem.model.attachment.Attachment;
import com.Anbu.TaskManagementSystem.model.attachment.AttachmentDTO;
import com.Anbu.TaskManagementSystem.model.attachment.AttachmentMapper;
import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.employee.EmployeeDetailsDTO;
import com.Anbu.TaskManagementSystem.model.employee.EmployeeMapper;
import com.Anbu.TaskManagementSystem.model.employee.EmploymentStatus;
import com.Anbu.TaskManagementSystem.model.project.NewProjectDTO;
import com.Anbu.TaskManagementSystem.model.project.Project;
import com.Anbu.TaskManagementSystem.model.project.ProjectDetailsDto;
import com.Anbu.TaskManagementSystem.model.project.ProjectMapper;
import com.Anbu.TaskManagementSystem.model.ticket.TicketMapper;
import com.Anbu.TaskManagementSystem.model.ticket.TicketRetrieveDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final AttachmentService attachmentService;
    private final ProjectMapper projectMapper;
    private final TicketMapper ticketMapper;
    private final EmployeeMapper employeeMapper;
    private final ProjectRepository projectRepository;
    private final EmployeeService employeeService;
    private final AttachmentMapper attachmentMapper;

    @Transactional
    public Project createNewProject(NewProjectDTO newProjectDTO) {

        if (projectRepository.existsByAcronym(newProjectDTO.getAcronym())) {
            throw new ProjectException.ProjectAlreadyExistsException("Acronym is Used by another project!!");
        }
        if (projectRepository.existsByProjectName(newProjectDTO.getProjectName())) {
            throw new ProjectException.ProjectAlreadyExistsException("Project Already exists!!");
        }
        Project newProject = new Project();

        newProject.setProjectName(newProjectDTO.getProjectName());
        newProject.setAcronym(newProjectDTO.getAcronym());
        return projectRepository.save(newProject);
    }

    public Project editProject(int projectId, NewProjectDTO projectUpdate) {
        Project updatedProject = getProjectById(projectId);

        if (projectRepository.existsByAcronymAndIdNot(projectUpdate.getAcronym(),projectId)) {
            throw new ProjectException.ProjectAlreadyExistsException("Acronym is Used by another project!!");
        }
        if (projectRepository.existsByProjectNameAndIdNot(projectUpdate.getProjectName(),projectId)) {
            throw new ProjectException.ProjectAlreadyExistsException("Project Already exists!!");
        }

        updatedProject.setProjectName(projectUpdate.getProjectName());
        updatedProject.setAcronym(projectUpdate.getAcronym());
        return projectRepository.save(updatedProject);
    }

    @Transactional
    public void addParticipantToProject(int projectId, int participantId, String role){
        Project project = getProjectById(projectId);
        Employee employee = employeeService.getEmployeeById(participantId);

        if(role.equalsIgnoreCase("admin")){
            checkIsEmployeeAddable(employee,project.getProjectAdmins(),role);
            project.getProjectAdmins().add(employee);

        }else if(role.equalsIgnoreCase("member")){
            checkIsEmployeeAddable(employee,project.getMembers(),role);
            project.getMembers().add(employee);

        }else {
            throw new ProjectException.InvalidInputException("Please Provide Valid Input");
        }
        projectRepository.save(project);
    }

    @Transactional
    public void removeParticipantFromProject(int projectId, int participantId, String role){
        Project project = getProjectById(projectId);
        if(role.equalsIgnoreCase("admin")){

            if(!project.getProjectAdmins().remove(employeeService.getEmployeeById(participantId))){
                throw new ProjectException.UserNotFoundException("User already not Admin of this Project");
            }
        }else if(role.equalsIgnoreCase("member")){

            if(!project.getMembers().remove(employeeService.getEmployeeById(participantId))){
                throw new ProjectException.UserNotFoundException("User already not Member of this Project");
            }
        }else {
            throw new ProjectException.InvalidInputException("Please Provide Valid Input");
        }
        projectRepository.save(project);
    }

    public Map<String,List<EmployeeDetailsDTO>> getAllProjectMembers(int projectId){
        Map<String,List<EmployeeDetailsDTO>> allMembers = new HashMap<>();
        Project project = getProjectById(projectId);
        allMembers.putIfAbsent("ProjectAdmins",project.getProjectAdmins().stream().map(employeeMapper::getIndividualEmployeeDetails).toList());
        allMembers.putIfAbsent("ProjectMembers",project.getMembers().stream().map(employeeMapper::getIndividualEmployeeDetails).toList());

        return allMembers;
    }

    public List<ProjectDetailsDto> getAllProjects(){
        return projectRepository.findAll().stream().map(projectMapper::retrieveProjectDetails).collect(Collectors.toList());
    }

    public List<TicketRetrieveDTO> getProjectTickets(String projAcronym){
        Project project = getProjectByAcronym(projAcronym);
        return project.getTickets().stream().map(ticketMapper::getTicket).collect(Collectors.toList());
    }

    void checkIsEmployeeAddable(Employee employee, List<Employee> targetList, String projectRole){

        if(employee.getEmpStatus().name().equalsIgnoreCase("inactive")){
            throw new ProjectException.EmployeeNotSuitableException("User is Inactive");
        }
        else if(employee.getRole().name().equalsIgnoreCase("admin")){
            throw new ProjectException.EmployeeNotSuitableException(employee.getUsername()+ "is an Admin. Can't be added");
        }
        else if(targetList.contains(employee)){
            throw new ProjectException.DuplicateRecordException("Employee is already "+projectRole.toLowerCase()+" of this Project");
        }
    }

    public void deleteProject(int projectId){
        projectRepository.deleteById(projectId);
    }

    public Project getProjectByAcronym(String projAcronym){
        return projectRepository.findByAcronym(projAcronym).orElseThrow(()->new ProjectException.ProjectNotFoundException("No such Project found"));
    }

    public Project getProjectById(int id){
        return projectRepository.findById(id).orElseThrow(()->new ProjectException.ProjectNotFoundException("No Project found"));
    }

    public List<AttachmentDTO> addAttachments(int projectId, List<MultipartFile> files) {
        if(files == null || files.isEmpty())
            throw new ProjectException.InvalidInputException("Files not selected");

        Project project = getProjectById(projectId);
        List<AttachmentDTO> attachmentDTOS = new ArrayList<>();

        for(MultipartFile file: files){
            Attachment attachment = attachmentService.saveFiles(project,file);
            attachmentDTOS.add(attachmentMapper.provideAttachmentDto(attachment));
        }
        return attachmentDTOS;
    }

}

