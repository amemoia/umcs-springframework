package com.umcsuser.carrent.models;

public class Rental {

    private String id;
    private String vehicleId;
    private String userId;
    private String rentDateTime;
    private String returnDateTime;

    public Rental() {}

    public Rental(String id, String vehicleId, String userId, String rentDateTime, String returnDateTime) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.userId = userId;
        this.rentDateTime = rentDateTime;
        this.returnDateTime = returnDateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRentDateTime() {
        return rentDateTime;
    }

    public void setRentDateTime(String rentDateTime) {
        this.rentDateTime = rentDateTime;
    }

    public String getReturnDateTime() {
        return returnDateTime;
    }

    public void setReturnDateTime(String returnDateTime) {
        this.returnDateTime = returnDateTime;
    }

    public Rental copy() {
        return new Rental(id, vehicleId, userId, rentDateTime, returnDateTime);
    }

    public boolean isActive() {
        return returnDateTime == null || returnDateTime.isBlank();
    }

    public String toCSV() {
        String returnDate = (returnDateTime == null || returnDateTime.isEmpty()) ? "null" : returnDateTime;
        return id + ";" + vehicleId + ";" + userId + ";" + rentDateTime + ";" + returnDate;
    }
}