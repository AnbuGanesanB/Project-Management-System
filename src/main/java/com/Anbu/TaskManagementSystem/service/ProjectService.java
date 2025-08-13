package com.Anbu.TaskManagementSystem.service;

import com.Anbu.TaskManagementSystem.Repository.ProjectRepository;
import com.Anbu.TaskManagementSystem.exception.ProjectException;
import com.Anbu.TaskManagementSystem.model.attachment.Attachment;
import com.Anbu.TaskManagementSystem.model.attachment.AttachmentDTO;
import com.Anbu.TaskManagementSystem.model.attachment.AttachmentMapper;
import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.employee.MapperDtos.EmployeeDetailDto;
import com.Anbu.TaskManagementSystem.model.employee.MapperDtos.EmployeeLimitedDetailsMapper;
import com.Anbu.TaskManagementSystem.model.employee.Role;
import com.Anbu.TaskManagementSystem.model.project.MapperDtos.ProjectDetailDto;
import com.Anbu.TaskManagementSystem.model.project.MapperDtos.ProjectOverallDetailMapper;
import com.Anbu.TaskManagementSystem.model.project.NewProjectDTO;
import com.Anbu.TaskManagementSystem.model.project.Project;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


@Service
@RequiredArgsConstructor
public class ProjectService {

    private final AttachmentService attachmentService;
    private final EmployeeLimitedDetailsMapper employeeLimitedDetailsMapper;
    private final ProjectRepository projectRepository;
    private final EmployeeService employeeService;
    private final AttachmentMapper attachmentMapper;
    private final ProjectOverallDetailMapper projectOverallDetailMapper;

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

    @Transactional
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

    public Map<String,List<EmployeeDetailDto>> getProjectParticipants(Project project){
        Map<String,List<EmployeeDetailDto>> allMembers = new HashMap<>();

        allMembers.putIfAbsent("ProjectAdmins",project.getProjectAdmins().stream().map(employeeLimitedDetailsMapper::getLimitedEmployeeDetails).toList());
        allMembers.putIfAbsent("ProjectMembers",project.getMembers().stream().map(employeeLimitedDetailsMapper::getLimitedEmployeeDetails).toList());

        return allMembers;
    }

    public List<Project> getAllProjects(){
        return projectRepository.findAll();
    }

    void checkIsEmployeeAddable(Employee employee, List<Employee> targetList, String projectRole){

        if(employee.getEmpStatus().name().equalsIgnoreCase("inactive")){
            throw new ProjectException.EmployeeNotSuitableException("User is Inactive");
        }
        else if(targetList.contains(employee)){
            throw new ProjectException.DuplicateRecordException("Employee is already "+projectRole.toLowerCase()+" of this Project");
        }
    }

    public ResponseEntity<Void> deleteProject(int projectId){
        if(projectRepository.findById(projectId).isEmpty()) return ResponseEntity.notFound().build();
        else {
            projectRepository.deleteById(projectId);
            return ResponseEntity.noContent().build();
        }
    }

    public Project getProjectById(int id){
        return projectRepository.findById(id).orElseThrow(()->new ProjectException.ProjectNotFoundException("No Project found"));
    }

    @Transactional
    public List<AttachmentDTO> addAttachments(int projectId, List<MultipartFile> files) {
        if(files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty))
            throw new ProjectException.InvalidInputException("Files not selected");

        Project project = getProjectById(projectId);
        List<AttachmentDTO> attachmentDTOS = new ArrayList<>();

        for(MultipartFile file: files){
            Attachment attachment = attachmentService.saveFiles(project,file);
            attachmentDTOS.add(attachmentMapper.provideAttachmentDto(attachment));
        }
        return attachmentDTOS;
    }

    public ProjectDetailDto getOverallProjectDetails(Project project){
        return projectOverallDetailMapper.getProjectDetails(project);
    }

    public boolean checkIsUserAllowedToViewProject(Project project, Employee employee) {
        if (employee.getRole() == Role.USER || employee.getRole() == Role.MANAGER) {
            return isPartOfProject(project, employee);
        }
        return true;
    }

    private boolean isPartOfProject(Project project, Employee employee) {
        return project.getProjectAdmins().stream().anyMatch(e -> e.getId().equals(employee.getId())) ||
                project.getMembers().stream().anyMatch(e -> e.getId().equals(employee.getId()));
    }

}

