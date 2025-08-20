package com.Anbu.TaskManagementSystem.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice("com.Anbu.TaskManagementSystem.controller")
@ControllerAdvice()
public class customExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private DataSize maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size}")
    private DataSize maxRequestSize;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleValidationException(MethodArgumentNotValidException exception){
        Map<String,String> errors = new HashMap<>();
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        for(FieldError objError: fieldErrors){
            errors.putIfAbsent(objError.getField(),objError.getDefaultMessage());
        }
        return errors;
    }


    @ExceptionHandler({
            EmployeeException.EmpIdAlreadyExistsException.class,
            EmployeeException.EmailAlreadyExistsException.class,
            ProjectException.ProjectAlreadyExistsException.class,
            TicketException.NoUpdationNeededException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictExceptions(RuntimeException ex) {
        return new ErrorResponse(ex.getMessage());
    }


    @ExceptionHandler({
            EmployeeException.PasswordNotCorrectException.class,
            ProjectException.EmployeeNotSuitableException.class,
            TicketException.UserNotAuthorisedException.class
    })
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponse handleNotAcceptableExceptions(RuntimeException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({
            UsernameNotFoundException.class,
            ProjectException.DuplicateRecordException.class,
            EmployeeException.NotValidInputException.class,
            ProjectException.InvalidInputException.class,
            TicketException.NotValidInputException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestExceptions(RuntimeException ex) {
        return new ErrorResponse(ex.getMessage());
    }


    @ExceptionHandler({
            ProjectException.ProjectNotFoundException.class,
            ProjectException.UserNotFoundException.class,
            TicketException.FileNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExceptions(RuntimeException ex) {
        return new ErrorResponse(ex.getMessage());
    }


    @ExceptionHandler({ AuthorizationDeniedException.class, AccessDeniedException.class })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleSecurityExceptions(Exception ex) {
        return new ErrorResponse(ex.getMessage());
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ErrorResponse handleMaxSizeException(MaxUploadSizeExceededException ex) {
        Throwable cause = ex.getCause();
        String errorMessage = cause.toString();

        if (errorMessage.contains("FileSizeLimitExceeded")) {
            return new ErrorResponse("Upload failed! Max allowed per file is " + maxFileSize.toMegabytes() + "MB");
        } else if (errorMessage.contains("SizeLimitExceeded")) {
            return new ErrorResponse("Upload failed! Max total request size is " + maxRequestSize.toMegabytes() + "MB");
        }
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(EmployeeException.EmpNotFoundException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleNoUserFoundException(RuntimeException ex){
        return new ErrorResponse(ex.getMessage());
    }


    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericRuntimeException(RuntimeException exception) {
        return new ErrorResponse(exception.getMessage());
    }

}
