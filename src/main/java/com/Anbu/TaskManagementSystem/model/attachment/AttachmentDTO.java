package com.Anbu.TaskManagementSystem.model.attachment;

import lombok.Data;

@Data
public class AttachmentDTO {

    private String originalFileName;
    private String uniqueFileName;
    private String downloadUrl;
}
