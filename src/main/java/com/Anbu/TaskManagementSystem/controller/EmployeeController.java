package com.Anbu.TaskManagementSystem.controller;

import com.Anbu.TaskManagementSystem.Repository.EmployeeRepo;
import com.Anbu.TaskManagementSystem.config.JwtAuthFilter;
import com.Anbu.TaskManagementSystem.model.employee.*;
import com.Anbu.TaskManagementSystem.service.EmployeeService;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

//@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeRepo employeeRepo;
    private final JwtAuthFilter jwtAuthFilter;
    private final EmployeeMapper employeeMapper;
    

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody EmployeeLoginDTO employeeLoginDTO){
        return employeeService.authenticateEmployee(employeeLoginDTO);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/employees")
    public EmployeeDetailsDTO createEmployee(@RequestBody @Valid EmployeeCreationDTO employeeCreationDTO){

        Employee employee = employeeService.addNewEmployee(employeeCreationDTO);
        return employeeMapper.getIndividualEmployeeDetails(employee);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping("/employees/{id}")
    public EmployeeDetailsDTO updateEmployeeDetails(@PathVariable("id") int id, @RequestBody @Valid EmployeeUpdationsDTO employeeUpdationsDTO){
        Employee employee = employeeService.updateEmployeeDetail(id, employeeUpdationsDTO);
        return employeeMapper.getIndividualEmployeeDetails(employee);
    }

    @GetMapping("/employees")
    public List<EmployeeDetailsDTO> getAllEmployeeDetails(@RequestParam(value = "status", required = false) String status,
                                                          @RequestParam(value = "role", required = false) String role,
                                                          Authentication authentication){

        List<String> requiredStatus = new ArrayList<>();
        List<String> requiredRoles = new ArrayList<>();

        // Required Status
        if(status == null || status.isEmpty()){
            requiredStatus.addAll(Arrays.asList("ACTIVE","INACTIVE"));      //By default - provides both users
        } else if(status.equalsIgnoreCase("ACTIVE") || status.equalsIgnoreCase("INACTIVE")) {
            requiredStatus.add(status.toUpperCase());
        }

        // Required Roles
        if (authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            if(role == null || role.isEmpty()){                             //By default - provides all users for admin
                requiredRoles.addAll(Arrays.asList("ADMIN","MANAGER","USER"));
            } else {
                requiredRoles.add(role.toUpperCase());
            }

        } else if(authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_MANAGER"))) {
            if(role == null || role.isEmpty()){                             //By default - provides all users except admin for Managers
                requiredRoles.addAll(Arrays.asList("MANAGER","USER"));
            } else if(role.equalsIgnoreCase("admin")){
                throw new AccessDeniedException("Manager can't view Admin Users");      //Manager not allowed to view admins
            }else {
                requiredRoles.add(role.toUpperCase());
            }
        }

        return employeeRepo.findByRoleAndEmpStatus(requiredRoles,requiredStatus)
                .stream()
                .map(employeeMapper::getIndividualEmployeeDetails)
                .collect(Collectors.toList());
    }

    @GetMapping("/employees/{id}")
    public EmployeeDetailsDTO getEmployeeDetail(@PathVariable int id, Authentication authentication){
        Employee employee = employeeService.getEmployeeById(id);
        if(authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_MANAGER"))
            && (employee.getRole().name().equalsIgnoreCase("ADMIN"))){
            throw new AccessDeniedException("Manager can't view Admin Users");
        }
        return employeeMapper.getIndividualEmployeeDetails(employee);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping("/me/password")
    public void updatePassword(@RequestBody PasswordChangeDTO passwordChangeDTO){
        employeeService.updatePassword(jwtAuthFilter.getCurrentUser(), passwordChangeDTO);
    }

    @GetMapping("/me")
    public EmployeeDetailsDTO getOwnDetail(Authentication authentication){
        Employee employee = (Employee)authentication.getPrincipal();
        return employeeMapper.getIndividualEmployeeDetails(employeeService.getEmployeeByEmpId(employee.getEmpId()));
    }

}
