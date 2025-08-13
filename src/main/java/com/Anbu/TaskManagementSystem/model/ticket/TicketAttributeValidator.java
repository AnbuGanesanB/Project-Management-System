package com.Anbu.TaskManagementSystem.model.ticket;

import com.Anbu.TaskManagementSystem.exception.TicketException;
import org.springframework.stereotype.Component;

@Component
public class TicketAttributeValidator {

    public void validateValue(TicketAttribute attribute, String value) {
        switch (attribute) {
            case STATUS -> {
                if(value==null || value.isBlank()) throw new TicketException.NotValidInputException("Need valid Status");
                try {
                    TicketStatus.valueOf(value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new TicketException.NotValidInputException("Invalid STATUS value: " + value);
                }
            }
            case TYPE -> {
                if(value==null || value.isBlank()) throw new TicketException.NotValidInputException("Need valid Type");
                System.out.println("Value:"+value);
                try {
                    TicketType.valueOf(value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new TicketException.NotValidInputException("Invalid TYPE value: " + value);
                }
            }
            case TITLE -> {
                if(value==null || value.isBlank()) throw new TicketException.NotValidInputException("Need valid Title");
            }
            case COMMENT -> {
                if(value==null || value.isBlank()) throw new TicketException.NotValidInputException("Need valid Comment");
            }
            case DESCRIPTION -> {
                if(value==null || value.isBlank()) throw new TicketException.NotValidInputException("Need valid Description");
            }
            case CREATE -> {

            }
            case ASSIGNEE -> {

            }
            default -> throw new UnsupportedOperationException("Validation not supported for: " + attribute);
        }
    }
}

