package com.Anbu.TaskManagementSystem.exception;

public class TicketException {
    public static class NoUpdationNeededException extends RuntimeException{
        public NoUpdationNeededException(String message){
            super(message);
        }
    }

    public static class UserNotAuthorisedException extends RuntimeException{
        public UserNotAuthorisedException(String message){
            super(message);
        }
    }

    public static class NotValidInputException extends RuntimeException{
        public NotValidInputException(String message){
            super(message);
        }
    }

    public static class FileNotFoundException extends RuntimeException{
        public FileNotFoundException(String message){
            super(message);
        }
    }
}
