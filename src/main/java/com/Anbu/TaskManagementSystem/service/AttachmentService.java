package com.Anbu.TaskManagementSystem.service;

import com.Anbu.TaskManagementSystem.Repository.AttachmentRepo;
import com.Anbu.TaskManagementSystem.Repository.TicketRepository;
import com.Anbu.TaskManagementSystem.model.attachment.Attachment;
import com.Anbu.TaskManagementSystem.model.ticket.Ticket;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepo attachmentRepo;
    private final TicketService ticketService;
    private final TicketRepository ticketRepository;

    @Transactional
    public void uploadFiles(Integer ticketId, List<MultipartFile> files) {
        Ticket currentTicket = ticketService.getTicketById(ticketId);
        for(MultipartFile file: files){
            if(!isSizeAllowed(file)){
                return;
            }
            try {
                byte[] bytes = file.getBytes();
                Attachment attachment = new Attachment();
                attachment.setFileName(file.getOriginalFilename());
                attachment.setFileBytes(bytes);
                attachment.setTicket(currentTicket);
                attachmentRepo.save(attachment);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        ticketRepository.save(currentTicket);
    }


    private boolean isSizeAllowed(MultipartFile file){
        if(!(file.getSize() < 500000)){
            throw new RuntimeException("File "+file.getOriginalFilename()+" size is Too large");
        }
        return true;
    }
}
