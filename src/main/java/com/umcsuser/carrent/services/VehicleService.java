package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.models.VehicleCategoryConfig;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.util.List;

public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;
    private final VehicleValidator vehicleValidator;

    public VehicleService(VehicleRepository vehicleRepository, RentalRepository rentalRepository, VehicleValidator vehicleValidator) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.vehicleValidator = vehicleValidator;
    }

    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.getVehicles();
    }

    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.getVehicles().stream()
                .filter(v -> !v.isRented())
                .toList();
    }

    public Vehicle findById(String id) {
        Vehicle vehicle = vehicleRepository.getVehicle(id);
        if (vehicle == null) {
            throw new RuntimeException("Pojazd o ID " + id + " nie istnieje.");
        }
        return vehicle;
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        return vehicleRepository.add(vehicle);
    }

    public void removeVehicle(String id) {
        if (!vehicleRepository.remove(id)) {
            throw new RuntimeException("Nie można usunąć pojazdu o ID " + id);
        }
    }

    public boolean isVehicleRented(String id) {
        Vehicle vehicle = vehicleRepository.getVehicle(id);
        return vehicle != null && vehicle.isRented();
    }
}
