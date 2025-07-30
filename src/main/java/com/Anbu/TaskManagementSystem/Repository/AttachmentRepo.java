package com.Anbu.TaskManagementSystem.Repository;

import com.Anbu.TaskManagementSystem.model.attachment.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttachmentRepo extends JpaRepository<Attachment,Integer> {

    Optional<Attachment> findByUniqueFileName(String uniqueFileName);
}
