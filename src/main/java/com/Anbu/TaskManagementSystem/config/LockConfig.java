package com.Anbu.TaskManagementSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.locks.ReentrantLock;

@Configuration
public class LockConfig {

    @Bean
    public ReentrantLock lock() {
        return new ReentrantLock(); // Creating a ReentrantLock bean
    }
}