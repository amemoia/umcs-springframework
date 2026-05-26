package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RentalService implements IRentalService {
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public RentalService(VehicleRepository vehicleRepository, RentalRepository rentalRepository, UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Rental> findAllRentals() {
        List<Rental> rentals = rentalRepository.findAll();
        rentals.forEach(this::attachReferences);
        return rentals;
    }

    @Override
    public List<Rental> findUserRentals(String userId) {
        return findAllRentals().stream()
                .filter(rental -> userId.equals(rental.getUserId()))
                .toList();
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return findAllRentals().stream()
                .filter(rental -> userId.equals(rental.getUserId()) && rental.isActive())
                .findFirst();
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        Optional<Rental> rental = rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId);
        rental.ifPresent(this::attachReferences);
        return rental.isPresent();
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        User user = findUserById(userId);
        Vehicle vehicle = vehicleRepository.getVehicle(vehicleId);

        if (vehicle == null) {
            throw new RuntimeException("Vehicle " + vehicleId + " does not exist");
        }
        if (vehicle.isRented() || vehicleHasActiveRental(vehicleId)) {
            throw new RuntimeException("Vehicle is already rented");
        }
        if (user.getRentedVehicleId() != null && !user.getRentedVehicleId().isBlank()) {
            throw new RuntimeException("User already has a rented vehicle");
        }

        Rental rental = new Rental(UUID.randomUUID().toString(), vehicle, user, LocalDateTime.now(), null);

        vehicle.setRented(true);
        user.setRentedVehicle(vehicle);

        vehicleRepository.update(vehicle);
        userRepository.update(user);
        rentalRepository.save(rental);

        return rental;
    }

    @Override
    public Rental returnVehicle(String userId) {
        User user = findUserById(userId);
        Rental rental = findActiveRentalByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No active rental found"));

        rental.setReturnDateTime(LocalDateTime.now());

        Vehicle vehicle = rental.getVehicle();
        if (vehicle == null && rental.getVehicleId() != null) {
            vehicle = vehicleRepository.getVehicle(rental.getVehicleId());
        }
        if (vehicle != null) {
            vehicle.setRented(false);
            vehicleRepository.update(vehicle);
        }

        user.setRentedVehicle(null);
        userRepository.update(user);
        return rentalRepository.save(rental);
    }

    private User findUserById(String userId) {
        return userRepository.getUsers().stream()
                .filter(user -> userId.equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User " + userId + " does not exist"));
    }

    private void attachReferences(Rental rental) {
        if (rental == null) {
            return;
        }
        if (rental.getVehicle() == null && rental.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.getVehicle(rental.getVehicleId());
            rental.setVehicle(vehicle);
        }
        if (rental.getUser() == null && rental.getUserId() != null) {
            User user = findUserById(rental.getUserId());
            rental.setUser(user);
        }
    }
}