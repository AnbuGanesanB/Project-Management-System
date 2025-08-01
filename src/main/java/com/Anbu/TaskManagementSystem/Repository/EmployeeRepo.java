package com.Anbu.TaskManagementSystem.Repository;

import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.employee.EmployeeUpdationsDTO;
import com.Anbu.TaskManagementSystem.model.employee.EmploymentStatus;
import com.Anbu.TaskManagementSystem.model.employee.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee,Integer> {

    Optional<Employee> findByUsername(String username);

    Optional<Employee> findByEmpId(String empId);

    Optional<Employee> findById(int id);

    Optional<Employee> findByEmail(String email);

    List<Employee> findAll();

    @Query("SELECT e FROM Employee e WHERE e.role IN :roles AND e.empStatus IN :status")
    List<Employee> findByRoleAndEmpStatus(@Param("roles") List<String> roles, @Param("status") List<String> status);

    List<Employee> findByEmpStatus(EmploymentStatus empStatus);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, int id);

    boolean existsByEmpId(String empId);
}
