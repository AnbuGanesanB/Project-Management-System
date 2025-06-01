package com.Anbu.TaskManagementSystem;

import com.Anbu.TaskManagementSystem.controller.EmployeeController;
import com.Anbu.TaskManagementSystem.model.employee.EmployeeCreationDTO;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TaskManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagementSystemApplication.class, args);
	}
}