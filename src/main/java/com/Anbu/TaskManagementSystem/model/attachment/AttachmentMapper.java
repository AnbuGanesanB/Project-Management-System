package com.Anbu.TaskManagementSystem.model.attachment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class AttachmentMapper {

    @Value("${api.file.path}")
    private String filePreviewPath;

    public AttachmentDTO provideAttachmentDto(Attachment attachment){
        AttachmentDTO attachmentDTO = new AttachmentDTO();

        attachmentDTO.setOriginalFileName(attachment.getOriginalFileName());
        attachmentDTO.setUniqueFileName(attachment.getUniqueFileName());

        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(filePreviewPath)
                .path(attachment.getUniqueFileName())
                .toUriString();

        System.out.println("Download url: "+downloadUrl);

        attachmentDTO.setDownloadUrl(downloadUrl);
        return attachmentDTO;
    }
}
