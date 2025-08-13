package com.Anbu.TaskManagementSystem.model.employee;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.Anbu.TaskManagementSystem.model.employee.Permission.*;

public enum Role {

    ADMIN(Set.of(
            EMPLOYEE_CREATE,
            EMPLOYEE_UPDATE,
            EMPLOYEE_VIEW,
            PROJECT_CREATE,
            PROJECT_UPDATE,
            PROJECT_VIEW,
            PROJECT_DELETE,
            TICKET_CREATE,
            TICKET_UPDATE,
            TICKET_VIEW,
            TICKET_DELETE,
            PROJECT_MANAGE,
            OWN_PROFILE)),

    MANAGER(Set.of(
            EMPLOYEE_VIEW,
            PROJECT_CREATE,
            PROJECT_UPDATE,
            PROJECT_VIEW,
            TICKET_CREATE,
            TICKET_UPDATE,
            TICKET_VIEW,
            TICKET_DELETE,
            PROJECT_MANAGE,
            OWN_PROFILE)),

    USER(Set.of(
            PROJECT_VIEW,
            TICKET_CREATE,
            TICKET_UPDATE,
            TICKET_VIEW,
            OWN_PROFILE));

    private final Set<Permission> permissions;


    Role(Set<Permission> permissions){
        this.permissions = permissions;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permissionEnum -> new SimpleGrantedAuthority(permissionEnum.name()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_"+this.name()));
        return authorities;
    }

    public Set<Permission> getPermissions(){
        return permissions;
    }

}
