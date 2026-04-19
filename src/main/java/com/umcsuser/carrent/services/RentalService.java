package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.models.Vehicle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.List;


public class RentalService {
    private VehicleRepository vehicleRepository;
    private RentalRepository rentalRepository;
    private UserRepository userRepository;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public RentalService(VehicleRepository vehicleRepository, RentalRepository rentalRepository, UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    public List<Rental> getRentals() {
        return rentalRepository.findAll();
    }

    public Rental rentVehicle(String userId, String vehicleId) {
        Vehicle vehicle = vehicleRepository.getVehicle(vehicleId);
        if (vehicle == null) {
            System.out.println("Vehicle not found");
            return null;
        }

        if (vehicle.isRented()) {
            System.out.println("Vehicle is already rented");
            return null;
        }

        User user = userRepository.getUser(userId);
        if (user == null) {
            System.out.println("User not found");
            return null;
        }

        if (user.getRentedVehicleId() != null && !user.getRentedVehicleId().isEmpty()) {
            System.out.println("User already has a rented vehicle");
            return null;
        }

        vehicle.setRented(true);
        vehicleRepository.update(vehicle);

        String rentalId = UUID.randomUUID().toString();
        String rentDateTime = LocalDateTime.now().format(formatter);
        Rental rental = new Rental(rentalId, vehicleId, userId, rentDateTime, null);
        rentalRepository.save(rental);

        user.setRentedVehicleId(vehicleId);
        userRepository.update(user);

        System.out.println("Vehicle rented successfully: " + vehicle);
        return rental;
    }

    public List<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }

    public List<Rental> findUserRentals(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId))
                .toList();
    }

    public java.util.Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId) && r.isActive())
                .findFirst();
    }

    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }

    public void returnVehicle(String userId) {
        Rental activeRental = findActiveRentalByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie posiada aktywnego wypożyczenia."));
        
        returnVehicle(userId, activeRental.getVehicleId());
    }

    public Rental returnVehicle(String userId, String vehicleId) {
        Vehicle vehicle = vehicleRepository.getVehicle(vehicleId);
        if (vehicle == null) {
            System.out.println("Vehicle not found");
            return null;
        }

        if (!vehicle.isRented()) {
            System.out.println("Vehicle is not rented");
            return null;
        }

        User user = userRepository.getUser(userId);
        if (user == null) {
            System.out.println("User not found");
            return null;
        }

        if (user.getRentedVehicleId() == null || !user.getRentedVehicleId().equals(vehicleId)) {
            System.out.println("User did not rent this vehicle");
            return null;
        }

        vehicle.setRented(false);
        vehicleRepository.update(vehicle);

        Rental rental = rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).orElse(null);
        if (rental == null) {
            System.out.println("No active rental found for this vehicle");
            return null;
        }

        String returnDateTime = LocalDateTime.now().format(formatter);
        rental.setReturnDateTime(returnDateTime);
        rentalRepository.save(rental);

        user.setRentedVehicleId(null);
        userRepository.update(user);

        System.out.println("Vehicle returned successfully: " + vehicle);
        return rental;
    }

    public List<Rental> getActiveRentals() {
        return rentalRepository.findAll().stream().filter(Rental::isActive).toList();
    }
    public List<Rental> getRentalsByUser(String userId) {
        return rentalRepository.findAll().stream().filter(r -> r.getUserId().equals(userId)).toList();
    }
}
