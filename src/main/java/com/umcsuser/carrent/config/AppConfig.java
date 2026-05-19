package com.umcsuser.carrent.config;

import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleCategoryConfigRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.repositories.impl.HibernateRentalRepository;
import com.umcsuser.carrent.repositories.impl.HibernateUserRepository;
import com.umcsuser.carrent.repositories.impl.HibernateVehicleRepository;
import com.umcsuser.carrent.repositories.impl.VehicleCategoryConfigJsonRepository;
import com.umcsuser.carrent.services.RentalService;
import com.umcsuser.carrent.services.UserService;
import com.umcsuser.carrent.services.VehicleCategoryConfigService;
import com.umcsuser.carrent.services.VehicleService;
import com.umcsuser.carrent.services.VehicleValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public VehicleCategoryConfigRepository vehicleCategoryConfigRepository() {
        return new VehicleCategoryConfigJsonRepository();
    }

    @Bean
    public VehicleRepository vehicleRepository() {
        return new HibernateVehicleRepository();
    }

    @Bean
    public UserRepository userRepository() {
        return new HibernateUserRepository();
    }

    @Bean
    public RentalRepository rentalRepository() {
        return new HibernateRentalRepository();
    }

    @Bean
    public VehicleCategoryConfigService vehicleCategoryConfigService(VehicleCategoryConfigRepository repository) {
        return new VehicleCategoryConfigService(repository);
    }

    @Bean
    public VehicleValidator vehicleValidator(VehicleCategoryConfigService categoryConfigService) {
        return new VehicleValidator(categoryConfigService);
    }

    @Bean
    public VehicleService vehicleService(VehicleRepository vehicleRepository,
                                         RentalRepository rentalRepository,
                                         VehicleValidator vehicleValidator) {
        return new VehicleService(vehicleRepository, rentalRepository, vehicleValidator);
    }

    @Bean
    public RentalService rentalService(VehicleRepository vehicleRepository,
                                       RentalRepository rentalRepository,
                                       UserRepository userRepository) {
        return new RentalService(vehicleRepository, rentalRepository, userRepository);
    }

    @Bean
    public UserService userService(UserRepository userRepository, RentalService rentalService) {
        return new UserService(userRepository, rentalService);
    }
}

