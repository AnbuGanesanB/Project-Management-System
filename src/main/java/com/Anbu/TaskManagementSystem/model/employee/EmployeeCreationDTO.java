package com.Anbu.TaskManagementSystem.model.employee;

import jakarta.validation.constraints.*;
import lombok.Data;


@Data
@NotNull
public class EmployeeCreationDTO {

    @NotBlank(message = "Username should not be blank")
    @NotEmpty(message = "Username should not be empty")
    private String username;

    @NotBlank(message = "Role should not be blank")
    @NotEmpty(message = "Role should not be empty")
    @Pattern(regexp = "^(?i)(ADMIN|USER|MANAGER)$", message = "please provide valid role (ADMIN/USER/MANAGER)")
    private String role;

    @NotBlank(message = "Employee ID should not be blank")
    @NotEmpty(message = "Employee ID should not be empty")
    @Pattern(regexp = "^[^@]{3,10}$",message = "Emp-ID should be 3-10 characters. should not include '@'")
    private String empId;

    @NotBlank(message = "Email should not be blank")
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Please enter valid email")
    private String email;

}
