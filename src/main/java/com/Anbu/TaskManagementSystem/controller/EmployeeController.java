package com.Anbu.TaskManagementSystem.controller;

import com.Anbu.TaskManagementSystem.model.employee.*;
import com.Anbu.TaskManagementSystem.model.employee.MapperDtos.EmployeeDetailDto;
import com.Anbu.TaskManagementSystem.model.employee.MapperDtos.EmployeeFullDetailsMapper;
import com.Anbu.TaskManagementSystem.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import static com.Anbu.TaskManagementSystem.config.ApiConstant.API_VERSION;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSION)
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeFullDetailsMapper employeeFullDetailsMapper;
    

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody EmployeeLoginDTO employeeLoginDTO){
        return employeeService.authenticateEmployee(employeeLoginDTO);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/employees")
    public EmployeeDetailDto createEmployee(@RequestBody @Valid EmployeeCreationDTO employeeCreationDTO){

        Employee employee = employeeService.addNewEmployee(employeeCreationDTO);
        return employeeService.getEmployeeFullDetails(employee);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("/employees/{id}")
    public EmployeeDetailDto updateEmployeeDetails(@PathVariable("id") int id, @RequestBody @Valid EmployeeUpdationsDTO employeeUpdationsDTO){
        Employee employee = employeeService.updateEmployeeDetail(id, employeeUpdationsDTO);
        return employeeService.getEmployeeFullDetails(employee);
    }

    @GetMapping("/employees")
    public List<EmployeeDetailDto> getAllEmployeeDetails(@RequestParam(value = "status", required = false) String status,
                                                          @RequestParam(value = "role", required = false) String role){

        return employeeService.getFilteredEmployees(status, role);
    }

    @GetMapping("/employees/{id}")
    public EmployeeDetailDto getEmployeeDetail(@PathVariable int id){
        Employee inquiredEmployee = employeeService.getEmployeeById(id);
        Employee requestingEmployee = employeeService.getCurrentUser();

        if(requestingEmployee.getRole()==Role.MANAGER && inquiredEmployee.getRole()==Role.ADMIN)
            throw new AccessDeniedException("Manager can't view Admin Users");

        return employeeFullDetailsMapper.getEmployeeFullDetails(inquiredEmployee);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping("/me/password")
    public void updatePassword(@RequestBody @Valid PasswordChangeDTO passwordChangeDTO){
        employeeService.updatePassword(employeeService.getCurrentUser(), passwordChangeDTO);
    }

    @GetMapping("/me")
    public EmployeeDetailDto getOwnDetail(){
        return employeeService.getEmployeeFullDetails(employeeService.getCurrentUser());
    }

}
