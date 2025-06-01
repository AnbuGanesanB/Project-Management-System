package com.Anbu.TaskManagementSystem.exception;

public class ProjectException {
    public static class DuplicateRecordException extends RuntimeException{
        public DuplicateRecordException(String message){
            super(message);
        }
    }

    public static class ProjectAlreadyExistsException extends RuntimeException{
        public ProjectAlreadyExistsException(String message){
            super(message);
        }
    }

    public static class ProjectNotFoundException extends RuntimeException{
        public ProjectNotFoundException(String message){super(message);}
    }

    public static class UserNotFoundException extends RuntimeException{
        public UserNotFoundException(String message){
            super(message);
        }
    }

    public static class EmployeeNotSuitableException extends RuntimeException{
        public EmployeeNotSuitableException(String message){
            super(message);
        }
    }
}
