package com.Anbu.TaskManagementSystem.service;

import com.Anbu.TaskManagementSystem.Repository.EmployeeRepo;
import com.Anbu.TaskManagementSystem.Repository.ProjectRepository;
import com.Anbu.TaskManagementSystem.exception.EmployeeException;
import com.Anbu.TaskManagementSystem.exception.ProjectException;
import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.employee.EmploymentStatus;
import com.Anbu.TaskManagementSystem.model.project.NewProjectDTO;
import com.Anbu.TaskManagementSystem.model.project.Project;
import com.Anbu.TaskManagementSystem.model.project.ProjectMapper;
import com.Anbu.TaskManagementSystem.model.ticket.TicketMapper;
import com.Anbu.TaskManagementSystem.model.ticket.TicketRetrieveDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final EmployeeRepo employeeRepo;
    private final ProjectMapper projectMapper;
    private final TicketMapper ticketMapper;
    private final ProjectRepository projectRepository;
    private final EmployeeService employeeService;

    @Transactional
    public void createNewProject(NewProjectDTO newProjectDTO) throws InterruptedException {

        /*lock.lock();  // Locking with ReentrantLock
        try {
            Thread.sleep(30000); // Simulating delay

            if (projectRepository.existsByAcronym(newProjectDTO.getAcronym())) {
                throw new ProjectException.ProjectAlreadyExistsException("Acronym is Used by another project!!");
            }

            if (projectRepository.existsByProjectName(newProjectDTO.getProjectName())) {
                throw new ProjectException.ProjectAlreadyExistsException("Project Already exists!!");
            }

            try {
                Project newProject = new Project();
                newProject.setProjectName(newProjectDTO.getProjectName());
                newProject.setAcronym(newProjectDTO.getAcronym());
                projectRepository.save(newProject);
            } catch (DataIntegrityViolationException e) {
                throw new DataIntegrityViolationException("An unknown error occurred during Project creation.");
            }
        } finally {
            lock.unlock();  // Unlocking the lock
        }*/

            if (projectRepository.existsByAcronym(newProjectDTO.getAcronym())) {
                throw new ProjectException.ProjectAlreadyExistsException("Acronym is Used by another project!!");
            }
            if (projectRepository.existsByProjectName(newProjectDTO.getProjectName())) {
                throw new ProjectException.ProjectAlreadyExistsException("Project Already exists!!");
            }
            try {
                Project newProject = new Project();
                newProject.setProjectName(newProjectDTO.getProjectName());
                newProject.setAcronym(newProjectDTO.getAcronym());
                projectRepository.save(newProject);
            } catch (DataIntegrityViolationException e) {
                throw new DataIntegrityViolationException("An unknown error occurred during Project creation.");
            }
    }

    @Transactional
    public void addAdminToProject(String projAcronym, String newAdmin){
        Project project = getProjectByAcronym(projAcronym);
        List<Employee> currentAdmins = project.getProjectAdmins();
        Employee employee = employeeService.getEmployeeByEmpId(newAdmin);
        if(isEmployeeAddable(employee,currentAdmins,"Admin")){
            currentAdmins.add(employee);
            project.setProjectAdmins(currentAdmins);
            projectRepository.save(project);
        }
    }

    @Transactional
    public void addMemberToProject(String projAcronym, String newMember) {
        Project project = getProjectByAcronym(projAcronym);
        List<Employee> currentMembers = project.getMembers();
        Employee employee = employeeService.getEmployeeByEmpId(newMember);
        if(isEmployeeAddable(employee,currentMembers,"Member")){
            currentMembers.add(employee);
            project.setMembers(currentMembers);
            projectRepository.save(project);
        }
    }

    @Transactional
    public void removeAdminFromProject(String projAcronym, String empId){
        Project project = getProjectByAcronym(projAcronym);
        if(!project.getProjectAdmins().remove(employeeService.getEmployeeByEmpId(empId))){
            throw new ProjectException.UserNotFoundException("User is not Admin of this Project");
        }
        projectRepository.save(project);
    }

    @Transactional
    public void removeMemberFromProject(String projAcronym, String empId){
        Project project = getProjectByAcronym(projAcronym);
        if(!project.getMembers().remove(employeeService.getEmployeeByEmpId(empId))){
            throw new ProjectException.UserNotFoundException("User is not Member of this Project");
        }
        projectRepository.save(project);
    }

    public List<String> getProjectAdmins(String projAcronym){
        Project project = getProjectByAcronym(projAcronym);
        return project.getProjectAdmins().stream().map(employee->employee.getEmpId()+":"+employee.getUsername()).toList();
    }

    public List<String> getProjectMembers(String projAcronym){
        Project project = getProjectByAcronym(projAcronym);
        return project.getMembers().stream().map(employee->employee.getEmpId()+":"+employee.getUsername()).toList();
    }

    public Map<String,List<String>> getAllProjectMembers(String projAcronym){
        Map<String,List<String>> allMembers = new HashMap<>();
        allMembers.putIfAbsent("ProjectAdmins",getProjectAdmins(projAcronym));
        allMembers.putIfAbsent("ProjectMembers",getProjectMembers(projAcronym));
        return allMembers;
    }

    public List<String> getNonAdmins(String projAcronym){
        Project project = getProjectByAcronym(projAcronym);
        List<Employee> allEmployees = employeeRepo.findByEmpStatus(EmploymentStatus.ACTIVE);
        return allEmployees.stream().filter(employee -> !(project.getProjectAdmins().contains(employee))).map(employee->employee.getEmpId()+":"+employee.getUsername()).collect(Collectors.toList());
    }

    public List<String> getNonMembers(String projAcronym){
        Project project = getProjectByAcronym(projAcronym);
        List<Employee> allEmployees = employeeRepo.findByEmpStatus(EmploymentStatus.ACTIVE);
        return allEmployees.stream().filter(employee -> !(project.getMembers().contains(employee))).map(employee->employee.getEmpId()+":"+employee.getUsername()).collect(Collectors.toList());
    }

    public Map<String,List<String>> getProjectNonParticipants(String projAcronym){
        Map<String,List<String>> nonParticipants = new HashMap<>();
        nonParticipants.putIfAbsent("Not_ProjectAdmins",getNonAdmins(projAcronym));
        nonParticipants.putIfAbsent("Not_ProjectMembers",getNonMembers(projAcronym));
        return nonParticipants;
    }

    public List<String> getAllProjects(){
        return projectRepository.findAll().stream().map(project -> project.getAcronym()+":"+project.getProjectName()).collect(Collectors.toList());
    }

    public List<TicketRetrieveDTO> getProjectTickets(String projAcronym){
        Project project = getProjectByAcronym(projAcronym);
        return project.getTickets().stream().map(ticketMapper::getTicket).collect(Collectors.toList());
    }

    boolean isEmployeeAddable(Employee employee, List<Employee> targetList, String projectRole){
        if(targetList.contains(employee)){
            throw new ProjectException.DuplicateRecordException("Employee is already "+projectRole+" of this Project");
        }
        else if(employee.getRole().name().equalsIgnoreCase("admin")){
            throw new ProjectException.EmployeeNotSuitableException("Admin user can't be addded");
        }
        else if(employee.getEmpStatus().name().equalsIgnoreCase("inactive")){
            throw new ProjectException.EmployeeNotSuitableException("User is Inactive");
        }
        return true;
    }

    public Project getProjectByAcronym(String projAcronym){
        return projectRepository.findByAcronym(projAcronym).orElseThrow(()->new ProjectException.ProjectNotFoundException("No such Project found"));
    }
}

