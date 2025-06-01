package com.Anbu.TaskManagementSystem.model.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmployeeUpdationsDTO {


    private String username;

    @Pattern(regexp = "^(?i)(ADMIN|USER|MANAGER)$", message = "please provide valid role")
    private String role;

    @Pattern(regexp = "^(?i)(ACTIVE|INACTIVE)$", message = "please provide valid status")
    private String status;

    @Email(message = "Please enter valid email")
    private String email;
}
