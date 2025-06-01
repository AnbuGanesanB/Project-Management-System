package com.Anbu.TaskManagementSystem.model.employee;

import lombok.Data;

@Data
public class PasswordChangeDTO {
    private String oldPassword;
    private String newPassword;
}
