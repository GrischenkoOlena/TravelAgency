package com.epam.finaltask.model;

import lombok.Getter;
import java.util.Set;
import static com.epam.finaltask.model.Permission.*;

@Getter
public enum Role {
    ADMIN(Set.of(ADMIN_CREATE, ADMIN_UPDATE, ADMIN_DELETE, ADMIN_READ)),
    MANAGER(Set.of(MANAGER_UPDATE)),
    USER(Set.of(USER_CREATE, USER_UPDATE, USER_DELETE, USER_READ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions){
        this.permissions = permissions;
    }
}
