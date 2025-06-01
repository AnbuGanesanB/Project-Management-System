package com.Anbu.TaskManagementSystem.model.attachment;

import com.Anbu.TaskManagementSystem.model.ticket.Ticket;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String fileName;
    private String mimeType;
    @Lob
    private byte[] fileBytes;
    @ManyToOne
    @JoinColumn(name = "ticket",nullable = false)
    private Ticket ticket;
    public String toString(){
        return "Hello";
    }
}
