package com.umcsuser.carrent.repositories;

import com.umcsuser.carrent.models.Vehicle;
import java.util.List;


public interface VehicleRepository {
    List<Vehicle> getVehicles();
    Vehicle getVehicle(String id);
    Vehicle add(Vehicle vehicle);
    Vehicle update(Vehicle vehicle);
    boolean remove(String id);
    void save();
    void load();
}