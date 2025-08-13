package com.Anbu.TaskManagementSystem.controller;

import com.Anbu.TaskManagementSystem.exception.TicketException;
import com.Anbu.TaskManagementSystem.model.attachment.Attachment;
import com.Anbu.TaskManagementSystem.service.AttachmentService;
import com.Anbu.TaskManagementSystem.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.Anbu.TaskManagementSystem.config.ApiConstant.API_VERSIONED_FILE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSIONED_FILE_PATH)
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final EmployeeService employeeService;

    @Value("${file.uploadDirectory}")
    private String uploadDirectory;

    @GetMapping("{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {

        try{

            Path fileStorageLocation = Paths.get(uploadDirectory).toAbsolutePath().normalize();
            Path targetPath = fileStorageLocation.resolve(filename).normalize();

            Resource resource = new UrlResource(targetPath.toUri());

            // Check that the resolved path is still under uploadDir
            // If not - reject the request
            if (!targetPath.startsWith(fileStorageLocation)) {
                return ResponseEntity.badRequest().build();
            }

            if (!resource.exists()) {
                throw new TicketException.FileNotFoundException("Requested file not found");
            }

            String contentType = Files.probeContentType(targetPath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            Attachment attachment = attachmentService.getAttachment(filename);
            String originalFileName = attachment.getOriginalFileName();

            attachmentService.checkUserAuthorised(employeeService.getCurrentUser(),attachment);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"")
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
