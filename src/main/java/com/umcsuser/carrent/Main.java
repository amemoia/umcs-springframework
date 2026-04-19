package com.umcsuser.carrent;

import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleCategoryConfigRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.repositories.impl.RentalRepositoryImpl;
import com.umcsuser.carrent.repositories.impl.UserRepositoryImpl;
import com.umcsuser.carrent.repositories.impl.VehicleCategoryConfigJsonRepository;
import com.umcsuser.carrent.repositories.impl.VehicleRepositoryImpl;
import com.umcsuser.carrent.services.AuthService;
import com.umcsuser.carrent.services.RentalService;
import com.umcsuser.carrent.services.UserService;
import com.umcsuser.carrent.services.VehicleCategoryConfigService;
import com.umcsuser.carrent.services.VehicleService;
import com.umcsuser.carrent.services.VehicleValidator;

public class Main {
    public static void main(String[] args) {
        VehicleRepository vehicleRepository = new VehicleRepositoryImpl();
        UserRepository userRepository = new UserRepositoryImpl();
        RentalRepository rentalRepository = new RentalRepositoryImpl();
        VehicleCategoryConfigRepository categoryConfigRepository = new VehicleCategoryConfigJsonRepository();

        AuthService authService = new AuthService(userRepository);
        VehicleCategoryConfigService categoryConfigService = new VehicleCategoryConfigService(categoryConfigRepository);
        VehicleValidator vehicleValidator = new VehicleValidator(categoryConfigService);
        VehicleService vehicleService = new VehicleService(vehicleRepository, rentalRepository, vehicleValidator);
        RentalService rentalService = new RentalService(vehicleRepository, rentalRepository, userRepository);
        UserService userService = new UserService(userRepository, rentalService);

        UI ui = new UI(
                authService,
                vehicleService,
                rentalService,
                userService,
                categoryConfigService
        );

        ui.start();
    }
}
