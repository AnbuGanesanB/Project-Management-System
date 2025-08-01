package com.Anbu.TaskManagementSystem.model.project;

import com.Anbu.TaskManagementSystem.model.attachment.Attachment;
import com.Anbu.TaskManagementSystem.model.employee.Employee;
import com.Anbu.TaskManagementSystem.model.ticket.Ticket;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    @JoinTable(name = "project_admins", joinColumns = @JoinColumn(name="project_id"),inverseJoinColumns = @JoinColumn(name = "pro_adm_id"))
    private List<Employee> projectAdmins = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinTable(name = "project_members", joinColumns = @JoinColumn(name="project_id"),inverseJoinColumns = @JoinColumn(name = "mem_id"))
    private List<Employee> members = new ArrayList<>();

    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL)
    private List<Attachment> attachment = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", projectName='" + projectName + '\'' +
                ", acronym='" + acronym + '\'' +
                '}';
    }

}
