package com.Anbu.TaskManagementSystem.exception;

import org.springframework.security.core.AuthenticationException;

public class JWT_Exception {

    public static class JwtAuthenticationException extends AuthenticationException {
        public JwtAuthenticationException(String msg) {
            super(msg);
        }
    }
}
