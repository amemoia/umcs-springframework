package cwiczenia;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


public class RentalService {
    private IVehicleRepository vehicleRepository;
    private UserRepository userRepository;
    private IRentalRepository rentalRepository;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public RentalService(IVehicleRepository vehicleRepository, UserRepository userRepository, IRentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
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
        vehicleRepository.add(vehicle);

        String rentalId = UUID.randomUUID().toString();
        String rentDateTime = LocalDateTime.now().format(formatter);
        Rental rental = new Rental(rentalId, vehicleId, userId, rentDateTime, null);
        rentalRepository.add(rental);

        user.setRentedVehicleId(vehicleId);
        userRepository.update(user);

        System.out.println("Vehicle rented successfully: " + vehicle);
        return rental;
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

        if (!user.getRentedVehicleId().equals(vehicleId)) {
            System.out.println("User did not rent this vehicle");
            return null;
        }

        vehicle.setRented(false);
        vehicleRepository.add(vehicle);

        Rental rental = rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId);
        if (rental == null) {
            System.out.println("No active rental found for this vehicle");
            return null;
        }

        String returnDateTime = LocalDateTime.now().format(formatter);
        rental.setReturnDateTime(returnDateTime);
        rentalRepository.update(rental);

        user.setRentedVehicleId(null);
        userRepository.update(user);

        System.out.println("Vehicle returned successfully: " + vehicle);
        return rental;
    }

    public java.util.List<Rental> getActiveRentals() {
        return rentalRepository.findActiveRentals();
    }
    public java.util.List<Rental> getRentalsByUser(String userId) {
        return rentalRepository.findByUserId(userId);
    }
}

