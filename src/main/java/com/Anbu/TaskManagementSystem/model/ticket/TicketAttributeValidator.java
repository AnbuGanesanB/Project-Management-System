package com.Anbu.TaskManagementSystem.model.ticket;

import com.Anbu.TaskManagementSystem.exception.TicketException;
import org.springframework.stereotype.Component;

@Component
public class TicketAttributeValidator {

    public void validateValue(TicketAttribute attribute, String value) {
        switch (attribute) {
            case STATUS -> {
                try {
                    TicketStatus.valueOf(value);
                } catch (IllegalArgumentException e) {
                    throw new TicketException.NotValidInputException("Invalid STATUS value: " + value);
                }
            }
            case TYPE -> {
                try {
                    TicketType.valueOf(value);
                } catch (IllegalArgumentException e) {
                    throw new TicketException.NotValidInputException("Invalid TYPE value: " + value);
                }
            }
            case TITLE,COMMENT -> {
                if(value.trim().length()<3) throw new TicketException.NotValidInputException("Field length should be more than 3 characters");
            }
            // Extend here if needed
            case ASSIGNEE, CREATE -> {
                // Accept any value or handle differently
                // Optional: throw if you're validating only specific attributes
            }
            default -> throw new UnsupportedOperationException("Validation not supported for: " + attribute);
        }
    }
}

