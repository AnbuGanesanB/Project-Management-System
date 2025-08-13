package com.Anbu.TaskManagementSystem.model.attachment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static com.Anbu.TaskManagementSystem.config.ApiConstant.API_VERSIONED_FILE_PATH;

@Component
@RequiredArgsConstructor
public class AttachmentMapper {

    @Value("${api.file.path}")
    private String filePreviewPath;

    public AttachmentDTO provideAttachmentDto(Attachment attachment){
        AttachmentDTO attachmentDTO = new AttachmentDTO();

        attachmentDTO.setId(attachment.getId());
        attachmentDTO.setOriginalFileName(attachment.getOriginalFileName());
        attachmentDTO.setUniqueFileName(attachment.getUniqueFileName());

        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(API_VERSIONED_FILE_PATH)
                .path(attachment.getUniqueFileName())
                .toUriString();

        attachmentDTO.setDownloadUrl(downloadUrl);
        return attachmentDTO;
    }
}
