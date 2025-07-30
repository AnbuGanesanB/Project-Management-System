package com.Anbu.TaskManagementSystem.service;

import com.Anbu.TaskManagementSystem.Repository.AttachmentRepo;
import com.Anbu.TaskManagementSystem.Repository.ProjectRepository;
import com.Anbu.TaskManagementSystem.Repository.TicketRepository;
import com.Anbu.TaskManagementSystem.model.attachment.Attachment;
import com.Anbu.TaskManagementSystem.model.project.Project;
import com.Anbu.TaskManagementSystem.model.ticket.Ticket;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    @Value("${file.uploadDirectory}")
    private String uploadDirectory;

    private final AttachmentRepo attachmentRepo;
    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public Attachment saveFiles(Ticket ticket, MultipartFile file) {
        return saveAttachment(file, attachment -> {
            attachment.setTicket(ticket);
            ticket.getAttachment().add(attachment);
            ticketRepository.save(ticket);
        });
    }

    @Transactional
    public Attachment saveFiles(Project project, MultipartFile file) {
        return saveAttachment(file, attachment -> {
            attachment.setProject(project);
            project.getAttachment().add(attachment);
            projectRepository.save(project);
        });
    }

    private Attachment saveAttachment(MultipartFile file, Consumer<Attachment> linkEntity) {
        Attachment attachment = new Attachment();
        try {
            String originalFilename = file.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

            Path uploadPath = Paths.get(uploadDirectory).resolve(uniqueFileName);
            Files.createDirectories(uploadPath.getParent());
            file.transferTo(uploadPath.toFile());

            attachment.setOriginalFileName(originalFilename);
            attachment.setUniqueFileName(uniqueFileName);
            attachment.setFilePath(uploadPath.toString());

            linkEntity.accept(attachment);

            attachment = attachmentRepo.save(attachment);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return attachment;
    }


    private boolean isSizeAllowed(MultipartFile file){
        if(!(file.getSize() < 500000)){
            throw new RuntimeException("File "+file.getOriginalFilename()+" size is Too large");
        }
        return true;
    }

    public String getOriginalFileName(String uniqueFileName){
        return attachmentRepo.findByUniqueFileName(uniqueFileName)
                .map(Attachment::getOriginalFileName)
                .orElse(null);
    }
}
