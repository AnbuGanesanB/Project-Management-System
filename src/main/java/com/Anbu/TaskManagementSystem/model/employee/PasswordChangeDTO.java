package com.Anbu.TaskManagementSystem.model.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@NotNull
public class PasswordChangeDTO {

    private String oldPassword;

    @NotNull(message = "Title should be mandatory")
    @NotBlank(message = "Password should not be blank")
    private String newPassword;
}
