package com.epam.finaltask.model;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.epam.finaltask.model.Permission.*;

@Getter
public enum Role {
    ADMIN(Set.of(ADMIN_CREATE, ADMIN_UPDATE, ADMIN_DELETE, ADMIN_READ)),
    MANAGER(Set.of(MANAGER_UPDATE, USER_READ, USER_UPDATE)),
    USER(Set.of(USER_CREATE, USER_UPDATE, USER_DELETE, USER_READ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions){
        this.permissions = permissions;
    }
    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> roleAuthorities = List.of(new SimpleGrantedAuthority("ROLE_" + this.name()));

        List<SimpleGrantedAuthority> permissionAuthorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList());

        return Stream.concat(roleAuthorities.stream(), permissionAuthorities.stream())
                .collect(Collectors.toList());
    }
}
