package com.Anbu.TaskManagementSystem.model.employee;

import com.Anbu.TaskManagementSystem.model.project.Project;
import com.Anbu.TaskManagementSystem.model.ticket.Ticket;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Employee implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String username;

    @Column(unique = true,nullable = false)
    private String empId;

    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentStatus empStatus;

    @ManyToMany(mappedBy = "projectAdmins", fetch = FetchType.LAZY)
    private List<Project> projectsManaging;

    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    private List<Project> projectsWorking;

    @OneToMany(mappedBy = "createdBy")
    private List<Ticket> createdTickets;

    @OneToMany(mappedBy = "assignee")
    private List<Ticket> assignedTickets;

    public String toString(){
        return "Hello";
    }

    //****************************************************************************************************

    public String getEmail(){
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override               //need from both (1)bcoz, to implement from 'UserDetails', (2) explicit getters, though not mandatory
    public String getPassword() {
        return password;
    }

    @Override               //need from both (1)bcoz, to implement from 'UserDetails', (2) explicit getters, though not mandatory
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;//UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;//UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;//UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return empStatus.name().equals("ACTIVE");
        //return true;//UserDetails.super.isEnabled();
    }
}
