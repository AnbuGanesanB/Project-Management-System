package com.Anbu.TaskManagementSystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice("com.Anbu.TaskManagementSystem.controller")
@ControllerAdvice()
public class customExceptionHandler {

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

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(EmployeeException.PasswordNotCorrectException.class)
    public String handlePasswordNotCorrectException(EmployeeException.PasswordNotCorrectException exception){
        return "error: "+exception.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EmployeeException.EmpIdAlreadyExistsException.class)
    public String handleEmpAlreadyExistsException(EmployeeException.EmpIdAlreadyExistsException exception) {
        return "error: "+exception.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EmployeeException.EmailAlreadyExistsException.class)
    public String handleEmailAlreadyExistsException(EmployeeException.EmailAlreadyExistsException exception) {
        return "error: "+exception.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUserNotFoundException(UsernameNotFoundException exception){
        return "error: "+exception.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ProjectException.ProjectAlreadyExistsException.class)
    public String handleProjAlreadyExistsException(ProjectException.ProjectAlreadyExistsException exception) {
        return "error: "+exception.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ProjectException.DuplicateRecordException.class)
    public String handleDuplicationRecordException(ProjectException.DuplicateRecordException exception) {
        return "error: "+exception.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProjectException.ProjectNotFoundException.class)
    public String handleProjectNotFoundException(ProjectException.ProjectNotFoundException exception){
        return "error: "+exception.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(ProjectException.EmployeeNotSuitableException.class)
    public String handleProjectNotFoundException(ProjectException.EmployeeNotSuitableException exception){
        return "error: "+exception.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(TicketException.UserNotAuthorisedException.class)
    public String handleUserNotAuthorisedException(TicketException.UserNotAuthorisedException exception){
        return "error: "+exception.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(TicketException.NoUpdationNeededException.class)
    public String handleNoUpdateNeededException(TicketException.NoUpdationNeededException exception){
        return "error: "+exception.getMessage();
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleGenericRuntimeException(RuntimeException exception) {
        return "error: "+exception.getMessage();
    }





}
