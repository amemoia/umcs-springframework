package com.umcsuser.carrent.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.UUID;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rented_vehicle_id")
    private Vehicle rentedVehicle;

    @Transient
    private String rentedVehicleId;

    public User() {
    }

    public User(String login, String password, String role) {
        this.id = UUID.randomUUID();
        this.login = login;
        this.password = password;
        this.role = Role.fromString(role);
        this.rentedVehicleId = null;
    }

    public User(String id, String login, String password, String role, String rentedVehicleId) {
        this.id = id != null ? UUID.fromString(id) : UUID.randomUUID();
        this.login = login;
        this.password = password;
        this.role = Role.fromString(role);
        this.rentedVehicleId = rentedVehicleId;
    }

    public String getId() {
        return id != null ? id.toString() : null;
    }

    public String getLogin() {
        return login;
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

    public Vehicle getRentedVehicle() {
        return rentedVehicle;
    }

    public void setRentedVehicle(Vehicle rentedVehicle) {
        this.rentedVehicle = rentedVehicle;
        this.rentedVehicleId = rentedVehicle != null ? rentedVehicle.getId() : null;
    }

    public String getRentedVehicleId() {
        if (rentedVehicle != null) {
            return rentedVehicle.getId();
        }
        return rentedVehicleId;
    }

    public void setRentedVehicleId(String vehicleId) {
        this.rentedVehicleId = vehicleId;
        if (vehicleId == null || vehicleId.isBlank() || "null".equalsIgnoreCase(vehicleId)) {
            this.rentedVehicle = null;
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toCSV() {
        String vehicleId = (getRentedVehicleId() == null || getRentedVehicleId().isEmpty()) ? "null" : getRentedVehicleId();
        return login + ";" + password + ";" + role + ";" + vehicleId;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", id='" + getId() + '\'' +
                ", role='" + role + '\'' +
                ", rentedVehicleId='" + getRentedVehicleId() + '\'' +
                '}';
    }
}
