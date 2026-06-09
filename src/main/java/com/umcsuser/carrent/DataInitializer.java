package com.umcsuser.carrent;

import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, VehicleRepository vehicleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeUser("admin", "admin123", Role.ADMIN, "c47afb0c-3c21-484b-88a3-8d7e0af2fb2f");
        initializeUser("user", "user123", Role.USER, "15e81a7f-373e-4903-bb76-d0b8bcafced7");
        initializeVehicle("65b0cdb4-58c7-419a-97bf-811af7f3fe41", "CAR", "Tesla", "Model S", 2022, 500.0f);
    }

    private void initializeUser(String login, String password, Role role, String id) {
        if (userRepository.getUser(login) == null) {
            User user = new User();
            user.setId(id);
            user.setLogin(login);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            userRepository.update(user);
            System.out.println("Initialized user: " + login);
        }
    }

    private void initializeVehicle(String id, String category, String brand, String model, int year, float price) {
        if (vehicleRepository.getVehicle(id) == null) {
            Vehicle vehicle = Vehicle.builder()
                    .id(id)
                    .category(category)
                    .brand(brand)
                    .model(model)
                    .year(year)
                    .price(price)
                    .build();
            vehicleRepository.add(vehicle);
            System.out.println("Initialized vehicle: " + brand + " " + model);
        }
    }
}
