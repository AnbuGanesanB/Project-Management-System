package com.Anbu.TaskManagementSystem.config;

import com.Anbu.TaskManagementSystem.Repository.EmployeeRepo;
import com.Anbu.TaskManagementSystem.exception.EmployeeException;
import com.Anbu.TaskManagementSystem.model.employee.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final EmployeeRepo employeeRepo;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        if(identifier.contains("@")) {
            return employeeRepo.findByEmail(identifier)
                    .orElseThrow(() -> new EmployeeException.EmpNotFoundException("User not found with email: " + identifier));
        }else{
            return employeeRepo.findByEmpId(identifier)
                    .orElseThrow(() -> new EmployeeException.EmpNotFoundException("User not found with Emp-Id: " + identifier));
        }
    }
}
