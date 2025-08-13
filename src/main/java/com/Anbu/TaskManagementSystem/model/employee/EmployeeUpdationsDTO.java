package com.Anbu.TaskManagementSystem.model.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmployeeUpdationsDTO {

    @NotBlank(message = "Username should not be blank")
    @NotEmpty(message = "Username should not be empty")
    private String username;

    @NotBlank(message = "Role should not be blank")
    @NotEmpty(message = "Role should not be empty")
    @Pattern(regexp = "^(?i)(ADMIN|USER|MANAGER)$", message = "please provide valid role (ADMIN/USER/MANAGER)")
    private String role;

    @NotBlank(message = "Status should not be blank")
    @NotEmpty(message = "Status should not be empty")
    @Pattern(regexp = "^(?i)(ACTIVE|INACTIVE)$", message = "please provide valid status (ACTIVE/INACTIVE)")
    private String status;

    @NotBlank(message = "Email should not be blank")
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Please enter valid email")
    private String email;
}
