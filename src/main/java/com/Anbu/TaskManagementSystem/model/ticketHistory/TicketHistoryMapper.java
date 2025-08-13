package com.Anbu.TaskManagementSystem.model.ticketHistory;

import com.Anbu.TaskManagementSystem.model.attachment.Attachment;
import com.Anbu.TaskManagementSystem.model.attachment.AttachmentMapper;
import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.employee.MapperDtos.EmployeeLimitedDetailsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketHistoryMapper {

    private final AttachmentMapper attachmentMapper;
    private final EmployeeLimitedDetailsMapper employeeLimitedDetailsMapper;

    public TicketHistoryDto retrieveTicketHistory(TicketHistory ticketHistory){
        TicketHistoryDto ticketHistoryDto = new TicketHistoryDto();

        ticketHistoryDto.setTicketId(ticketHistory.getTicket().getId());
        ticketHistoryDto.setHistoryId(ticketHistory.getId());
        ticketHistoryDto.setTicketAttribute(ticketHistory.getTicketAttribute().name());
        ticketHistoryDto.setUpdatedBy(employeeLimitedDetailsMapper.getLimitedEmployeeDetails(ticketHistory.getUpdatedBy()));
        ticketHistoryDto.setUpdatedOn(ticketHistory.getUpdatedOn());
        ticketHistoryDto.setOldValue(ticketHistory.getOldValue());
        ticketHistoryDto.setNewValue(ticketHistory.getNewValue());

        Employee assignee = ticketHistory.getAssignee();
        ticketHistoryDto.setAssignee(assignee != null ? employeeLimitedDetailsMapper.getLimitedEmployeeDetails(assignee) : null);

        Attachment attachment = ticketHistory.getAttachment();
        ticketHistoryDto.setAttachment(attachment != null ? attachmentMapper.provideAttachmentDto(attachment) : null);

        return ticketHistoryDto;
    }
}
