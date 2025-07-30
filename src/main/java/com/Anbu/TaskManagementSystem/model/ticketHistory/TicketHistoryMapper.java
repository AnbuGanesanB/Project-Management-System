package com.Anbu.TaskManagementSystem.model.ticketHistory;

import com.Anbu.TaskManagementSystem.model.attachment.Attachment;
import com.Anbu.TaskManagementSystem.model.attachment.AttachmentMapper;
import com.Anbu.TaskManagementSystem.model.employee.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketHistoryMapper {

    private final AttachmentMapper attachmentMapper;

    public TicketHistoryDto retrieveTicketHistory(TicketHistory ticketHistory){
        TicketHistoryDto ticketHistoryDto = new TicketHistoryDto();

        ticketHistoryDto.setTicketId(ticketHistory.getTicket().getId());
        ticketHistoryDto.setTicketAttribute(ticketHistory.getTicketAttribute().name());
        ticketHistoryDto.setUpdatedBy(ticketHistory.getUpdatedBy().getUsername());
        ticketHistoryDto.setUpdatedOn(ticketHistory.getUpdatedOn());
        ticketHistoryDto.setOldValue(ticketHistory.getOldValue());
        ticketHistoryDto.setNewValue(ticketHistory.getNewValue());

        Employee assignee = ticketHistory.getAssignee();
        ticketHistoryDto.setAssignee(assignee != null ? assignee.getUsername() : null);

        Attachment attachment = ticketHistory.getAttachment();
        ticketHistoryDto.setAttachment(attachment != null ? attachmentMapper.provideAttachmentDto(attachment) : null);

        return ticketHistoryDto;
    }
}
