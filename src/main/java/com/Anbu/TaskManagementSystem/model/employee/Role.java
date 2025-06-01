package com.Anbu.TaskManagementSystem.model.employee;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.Anbu.TaskManagementSystem.model.employee.Permission.*;

public enum Role {

    USER(Set.of(
            OWN_PROFILE,
            PROJECT_VIEW,
            TICKET_CREATE,
            TICKET_UPDATE,
            TICKET_VIEW,
            TICKET_DELETE)),
    ADMIN(Set.of(
            OWN_PROFILE,
            PROJECT_VIEW,
            EMPLOYEE_CREATE,
            EMPLOYEE_UPDATE,
            EMPLOYEE_VIEW)),
    MANAGER(Set.of(
            OWN_PROFILE,
            EMPLOYEE_VIEW,
            PROJECT_CREATE,
            PROJECT_UPDATE,
            PROJECT_VIEW,
            TICKET_CREATE,
            TICKET_UPDATE,
            TICKET_VIEW,
            TICKET_DELETE,
            PROJECT_MEMBERS_MANAGE));

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
