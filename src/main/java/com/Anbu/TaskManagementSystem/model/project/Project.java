package com.Anbu.TaskManagementSystem.model.project;

import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.ticket.Ticket;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String projectName;

    @Column(nullable = false, unique = true)
    private String acronym;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinTable(name = "project_members", joinColumns = @JoinColumn(name="project_id"),inverseJoinColumns = @JoinColumn(name = "mem_id"))
    private List<Employee> members;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinTable(name = "project_admins", joinColumns = @JoinColumn(name="project_id"),inverseJoinColumns = @JoinColumn(name = "pro_adm_id"))
    private List<Employee> projectAdmins;

    @OneToMany(mappedBy = "project")
    private List<Ticket> tickets;

    public String toString(){
        return "Hello";
    }

}
