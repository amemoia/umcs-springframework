package com.umcsuser.carrent.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name = "rentals")
public class Rental {

    private static final DateTimeFormatter SQL_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rent_date_time", nullable = false)
    private LocalDateTime rentDateTime;

    @Column(name = "return_date_time")
    private LocalDateTime returnDateTime;

    @Transient
    private String vehicleId;

    @Transient
    private String userId;

    public Rental() {
    }

    public Rental(String id, String vehicleId, String userId, String rentDateTime, String returnDateTime) {
        setId(id);
        setVehicleId(vehicleId);
        setUserId(userId);
        setRentDateTime(rentDateTime);
        setReturnDateTime(returnDateTime);
    }

    public Rental(String id, Vehicle vehicle, User user, LocalDateTime rentDateTime, LocalDateTime returnDateTime) {
        setId(id);
        this.vehicle = vehicle;
        this.user = user;
        this.rentDateTime = rentDateTime;
        this.returnDateTime = returnDateTime;
    }

    public String getId() {
        return id != null ? id.toString() : null;
    }

    public void setId(String id) {
        this.id = id != null ? UUID.fromString(id) : null;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.vehicleId = vehicle != null ? vehicle.getId() : null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public String getVehicleId() {
        if (vehicle != null) {
            return vehicle.getId();
        }
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
        if (vehicleId == null || vehicleId.isBlank() || "null".equalsIgnoreCase(vehicleId)) {
            this.vehicle = null;
        }
    }

    public String getUserId() {
        if (user != null) {
            return user.getId();
        }
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        if (userId == null || userId.isBlank() || "null".equalsIgnoreCase(userId)) {
            this.user = null;
        }
    }

    public String getRentDateTime() {
        return rentDateTime != null ? rentDateTime.format(SQL_FORMAT) : null;
    }

    public LocalDateTime getRentDateTimeValue() {
        return rentDateTime;
    }

    public void setRentDateTime(String rentDateTime) {
        this.rentDateTime = parseDateTime(rentDateTime);
    }

    public void setRentDateTime(LocalDateTime rentDateTime) {
        this.rentDateTime = rentDateTime;
    }

    public String getReturnDateTime() {
        return returnDateTime != null ? returnDateTime.format(SQL_FORMAT) : null;
    }

    public LocalDateTime getReturnDateTimeValue() {
        return returnDateTime;
    }

    public void setReturnDateTime(String returnDateTime) {
        this.returnDateTime = parseDateTime(returnDateTime);
    }

    public void setReturnDateTime(LocalDateTime returnDateTime) {
        this.returnDateTime = returnDateTime;
    }

    public Rental copy() {
        return new Rental(getId(), getVehicleId(), getUserId(), getRentDateTime(), getReturnDateTime());
    }

    public boolean isActive() {
        return returnDateTime == null;
    }

    public String toCSV() {
        String returnDate = (getReturnDateTime() == null || getReturnDateTime().isEmpty()) ? "null" : getReturnDateTime();
        return getId() + ";" + getVehicleId() + ";" + getUserId() + ";" + getRentDateTime() + ";" + returnDate;
    }

    @Override
    public String toString() {
        return "Rental{" +
                "id='" + getId() + '\'' +
                ", vehicleId='" + getVehicleId() + '\'' +
                ", userId='" + getUserId() + '\'' +
                ", rentDateTime='" + getRentDateTime() + '\'' +
                ", returnDateTime='" + getReturnDateTime() + '\'' +
                '}';
    }

    private static LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank() || "null".equalsIgnoreCase(value)) {
            return null;
        }
        if (value.contains("T")) {
            return LocalDateTime.parse(value);
        }
        return LocalDateTime.parse(value, SQL_FORMAT);
    }
}