package com.epam.finaltask.model;

public enum Permission {
    ADMIN_READ("read"),
    ADMIN_UPDATE("update"),
    ADMIN_CREATE("create"),
    ADMIN_DELETE("delete"),
    MANAGER_UPDATE("update"),
    USER_READ("read"),
    USER_UPDATE("update"),
    USER_CREATE("create"),
    USER_DELETE("delete");

    private final String permission;

    Permission(String permission){
        this.permission = permission;
    }
}
