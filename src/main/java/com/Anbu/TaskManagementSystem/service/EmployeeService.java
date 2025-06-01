package com.Anbu.TaskManagementSystem.service;

import com.Anbu.TaskManagementSystem.Repository.EmployeeRepo;
import com.Anbu.TaskManagementSystem.Repository.ProjectRepository;
import com.Anbu.TaskManagementSystem.config.JwtAuthFilter;
import com.Anbu.TaskManagementSystem.exception.EmployeeException;
import com.Anbu.TaskManagementSystem.model.employee.*;
import com.Anbu.TaskManagementSystem.model.project.Project;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final EmployeeRepo employeeRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ProjectRepository projectRepository;
    private final JwtAuthFilter jwtAuthFilter;

    Employee currentEmployee = null;

    public ResponseEntity<Map<String, String>> authenticateEmployee(EmployeeLoginDTO employeeLoginDTO) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(employeeLoginDTO.getEmail(), employeeLoginDTO.getPassword()));

            System.out.println("Authentication result: " + authentication);

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
            errorResponse.put("error", "Invalid email or password.");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);

        } catch (DisabledException ex) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Your account is disabled. Please contact support.");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }
    }

    @Transactional
    public void addNewEmployee(EmployeeCreationDTO employeeCreationDTO){
        if (employeeRepo.existsByEmail(employeeCreationDTO.getEmail())) {
            throw new EmployeeException.EmailAlreadyExistsException("Email already exists. Please choose another.");
        }
        if (employeeRepo.existsByEmpId(employeeCreationDTO.getEmpId())) {
            throw new EmployeeException.EmpIdAlreadyExistsException("Employee ID already exists. Please choose another.");
        }
        try {
            Employee employee = employeeMapper.createEmployeeFromDto(employeeCreationDTO);
            employeeRepo.save(employee);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("An unknown error occurred during employee creation.");
        }
    }

    public boolean isEmployeeAvailable(Employee employee){
        Optional<Employee> optionalEmployee = employeeRepo.findByUsername(employee.getUsername());
        return optionalEmployee.isPresent();
    }

    @Transactional
    public void updateEmployeeDetail(String empId, EmployeeUpdationsDTO employeeUpdationsDTO){

        Employee employee = getEmployeeByEmpId(empId);
        if(employeeUpdationsDTO.getUsername() != null){
            employee.setUsername(employeeUpdationsDTO.getUsername());
        }
        if(employeeUpdationsDTO.getRole() != null){
            employee.setRole(Role.valueOf(employeeUpdationsDTO.getRole().toUpperCase()));
        }
        if(employeeUpdationsDTO.getStatus() != null){
            employee.setEmpStatus(EmploymentStatus.valueOf(employeeUpdationsDTO.getStatus().toUpperCase()));
        }
        if(employeeUpdationsDTO.getEmail() != null){
            if (employeeRepo.existsByEmail(employeeUpdationsDTO.getEmail())) {
                throw new EmployeeException.EmailAlreadyExistsException("Email already exists. Please choose another.");
            }
            employee.setEmail(employeeUpdationsDTO.getEmail());
        }
        try{
            employeeRepo.save(employee);
        }catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("An unknown error occurred during employee creation.");
        }
    }

    @Transactional
    public void updatePassword(String currentUsername, PasswordChangeDTO passwordChangeDTO) {
        Employee employee = employeeRepo.findByUsername(currentUsername)
                .orElseThrow(()->new UsernameNotFoundException("User not found"));
        if(passwordChangeDTO.getOldPassword().equals(employee.getPassword())){
            employee.setPassword(passwordChangeDTO.getNewPassword());
            employeeRepo.save(employee);
        }
        else{
            System.out.println("Your old password is not correct");
            throw new EmployeeException.PasswordNotCorrectException("Old password is not correct");
        }
    }

    public Employee getEmployeeByEmpId(String empId){
       return employeeRepo.findByEmpId(empId)
               .orElseThrow(()->new UsernameNotFoundException("User not found"));
    }

    public Employee getCurrentUser(){
        return employeeRepo.findByUsername(jwtAuthFilter.getCurrentUser())
                .orElseThrow(()->new UsernameNotFoundException("User not found"));
    }

}
