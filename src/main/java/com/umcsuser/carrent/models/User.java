package com.umcsuser.carrent.models;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User() {
    }

    public User(String login, String password, String role) {
        this.id = UUID.randomUUID();
        this.login = login;
        this.password = password;
        this.role = Role.fromString(role);
    }

    public User(String id, String login, String password, String role) {
        this.id = id != null ? UUID.fromString(id) : UUID.randomUUID();
        this.login = login;
        this.password = password;
        this.role = Role.fromString(role);
    }

    public String getId() {
        return id != null ? id.toString() : null;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setId(String id) {
        this.id = id != null ? UUID.fromString(id) : null;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setRole(String role) {
        this.role = Role.fromString(role);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", id='" + getId() + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
