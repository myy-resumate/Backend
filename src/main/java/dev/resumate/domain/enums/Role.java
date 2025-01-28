package dev.resumate.domain.enums;

public enum Role {

    MEMBER("MEMBER"), ADMIN("ADMIN");

    private final String role;

    Role(String role) {
        this.role = role;
    }
}
