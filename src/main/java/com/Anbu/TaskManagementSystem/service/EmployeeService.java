package com.Anbu.TaskManagementSystem.service;

import com.Anbu.TaskManagementSystem.Repository.EmployeeRepo;
import com.Anbu.TaskManagementSystem.exception.EmployeeException;
import com.Anbu.TaskManagementSystem.model.employee.*;
import com.Anbu.TaskManagementSystem.model.employee.MapperDtos.EmployeeDetailDto;
import com.Anbu.TaskManagementSystem.model.employee.MapperDtos.EmployeeFullDetailsMapper;
import com.Anbu.TaskManagementSystem.model.project.Project;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepo employeeRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmployeeFullDetailsMapper employeeFullDetailsMapper;
    private final PasswordEncoder passwordEncoder;

    Employee currentEmployee = null;

    public ResponseEntity<Map<String, String>> authenticateEmployee(EmployeeLoginDTO employeeLoginDTO) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(employeeLoginDTO.getIdentifier(), employeeLoginDTO.getPassword()));

            currentEmployee = (Employee)authentication.getPrincipal();
            String token = jwtService.generateToken(currentEmployee);
            System.out.println(currentEmployee.getUsername()+":"+token);

            Map<String,String> response = new HashMap<>();
            response.putIfAbsent("Token",token);
            response.putIfAbsent("Username",currentEmployee.getUsername());
            response.putIfAbsent("Role",currentEmployee.getRole().name());

            return new ResponseEntity<>(response,HttpStatus.OK);

        }catch (BadCredentialsException ex) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid credentials.");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);

        } catch (DisabledException ex) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Your account is disabled. Please contact support.");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);

        }
    }

    @Transactional
    public Employee addNewEmployee(EmployeeCreationDTO employeeCreationDTO){
        if (employeeRepo.existsByEmail(employeeCreationDTO.getEmail())) {
            throw new EmployeeException.EmailAlreadyExistsException("Email already exists. Please choose another.");
        }
        if (employeeRepo.existsByEmpId(employeeCreationDTO.getEmpId())) {
            throw new EmployeeException.EmpIdAlreadyExistsException("Employee ID already exists. Please choose another.");
        }
        Employee employee = new Employee();

        employee.setUsername(employeeCreationDTO.getUsername());
        employee.setPassword(passwordEncoder.encode(employeeCreationDTO.getEmpId()));
        employee.setRole(Role.valueOf(employeeCreationDTO.getRole().toUpperCase()));
        employee.setEmpId(employeeCreationDTO.getEmpId());
        employee.setEmpStatus(EmploymentStatus.ACTIVE);
        employee.setEmail(employeeCreationDTO.getEmail());

        return employeeRepo.save(employee);
    }

    @Transactional
    public Employee updateEmployeeDetail(int id, EmployeeUpdationsDTO employeeUpdationsDTO){

        Employee employee = getEmployeeById(id);

        if (employeeRepo.existsByEmailAndIdNot(employeeUpdationsDTO.getEmail(),id)) {
            throw new EmployeeException.EmailAlreadyExistsException("Email already exists. Please provide another.");
        }
        employee.setEmail(employeeUpdationsDTO.getEmail());
        employee.setUsername(employeeUpdationsDTO.getUsername());
        employee.setRole(Role.valueOf(employeeUpdationsDTO.getRole().toUpperCase()));
        employee.setEmpStatus(EmploymentStatus.valueOf(employeeUpdationsDTO.getStatus().toUpperCase()));

        return employeeRepo.save(employee);
    }

    @Transactional
    public void updatePassword(Employee employee, PasswordChangeDTO passwordChangeDTO) {
        String oldPasswordFromDto = passwordChangeDTO.getOldPassword();
        String oldPasswordFromDB = employee.getPassword();

        if(passwordEncoder.matches(oldPasswordFromDto,oldPasswordFromDB)){
            employee.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
            employeeRepo.save(employee);
        }
        else{
            throw new EmployeeException.PasswordNotCorrectException("Old password is not correct");
        }
    }

    @Transactional
    public Employee getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Employee)authentication.getPrincipal();
    }

    public Employee getEmployeeById(int id){
        return employeeRepo.findById(id)
                .orElseThrow(()->new UsernameNotFoundException("User not found"));
    }

    public List<EmployeeDetailDto> getFilteredEmployees(String statusParam, String roleParam) {
        List<EmploymentStatus> requiredStatus = resolveStatusFilter(statusParam);
        List<Role> requiredRoles = resolveRoleFilter(roleParam,getCurrentUser());

        return employeeRepo.findByRoleAndEmpStatus(requiredRoles, requiredStatus)
                .stream()
                .map(employeeFullDetailsMapper::getEmployeeFullDetails)
                .collect(Collectors.toList());
    }

    private List<EmploymentStatus> resolveStatusFilter(String statusParam) {
        if (statusParam == null || statusParam.isEmpty()) {
            return List.of(EmploymentStatus.ACTIVE, EmploymentStatus.INACTIVE);
        }
        try {
            return List.of(EmploymentStatus.valueOf(statusParam.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new EmployeeException.NotValidInputException("Invalid status value: " + statusParam);
        }
    }

    private List<Role> resolveRoleFilter(String roleParam,Employee currentEmployee) {

        boolean isAdmin   = currentEmployee.getRole()==Role.ADMIN;
        boolean isManager = currentEmployee.getRole()==Role.MANAGER;

        if (isAdmin) {
            return (roleParam == null || roleParam.isEmpty())
                    ? List.of(Role.ADMIN, Role.MANAGER, Role.USER)
                    : List.of(parseRole(roleParam));
        }

        if (isManager) {
            if (roleParam == null || roleParam.isEmpty()) {
                return List.of(Role.MANAGER, Role.USER);
            }
            Role requestedRole = parseRole(roleParam);
            if (requestedRole == Role.ADMIN) {
                throw new AccessDeniedException("Manager can't view Admin Users");
            }
            return List.of(requestedRole);
        }

        throw new AccessDeniedException("Access denied");
    }

    private Role parseRole(String roleParam) {
        try {
            return Role.valueOf(roleParam.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EmployeeException.NotValidInputException("Invalid role value: " + roleParam);
        }
    }

    @Transactional
    public EmployeeDetailDto getEmployeeFullDetails(Employee employee){
        Employee currentUser = getEmployeeById(employee.getId());
        return employeeFullDetailsMapper.getEmployeeFullDetails(currentUser);
    }

    @Transactional
    public List<Project> getParticipatingProjects(Employee employee){

        Employee currentUser = getEmployeeById(employee.getId());

        return Stream.concat(currentUser.getProjectsManaging().stream(), currentUser.getProjectsWorking().stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
