package com.Anbu.TaskManagementSystem.exception;

public class EmployeeException {

    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class EmpIdAlreadyExistsException extends RuntimeException {
        public EmpIdAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class PasswordNotCorrectException extends RuntimeException{
        public PasswordNotCorrectException(String message){
            super(message);
        }
    }
}
