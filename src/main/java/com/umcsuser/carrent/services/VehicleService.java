package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VehicleService implements IVehicleService {
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
            throw new RuntimeException("Vehicle  " + id + " does not exist");
        }
        return vehicle;
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        return vehicleRepository.add(vehicle);
    }

    public void removeVehicle(String id) {
        Vehicle vehicle = findById(id);
        if (vehicle.isRented()) {
            throw new RuntimeException("Cannot delete a vehicle that is currently rented");
        }
        rentalRepository.deleteByVehicleId(id);
        if (!vehicleRepository.remove(id)) {
            throw new RuntimeException("Couldnt remove vehicle " + id);
        }
    }

    public boolean isVehicleRented(String id) {
        Vehicle vehicle = vehicleRepository.getVehicle(id);
        return vehicle != null && vehicle.isRented();
    }
}