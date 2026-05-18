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
import com.umcsuser.carrent.repositories.impl.*;

public class Main {
    public static void main(String[] args) {
        String repoType = "hibernate";

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("json") || args[0].equalsIgnoreCase("jdbc") || args[0].equalsIgnoreCase("hibernate")) {
                repoType = args[0].toLowerCase();
            }
        }

        VehicleRepository vehicleRepository;
        UserRepository userRepository;
        RentalRepository rentalRepository;
        VehicleCategoryConfigRepository categoryConfigRepository = new VehicleCategoryConfigJsonRepository();

        if ("hibernate".equals(repoType)) {
            String dbUrl = System.getenv("DB");
            if (dbUrl == null || dbUrl.isEmpty()) {
                System.err.println("where env.DB?");
                return;
            }
            HibernateUtil.getSessionFactory();
            vehicleRepository = new HibernateVehicleRepository();
            userRepository = new HibernateUserRepository();
            rentalRepository = new HibernateRentalRepository();
            System.out.println("using Hibernate...");
        } else if ("jdbc".equals(repoType)) {
            String dbUrl = System.getenv("DB");
            if (dbUrl == null || dbUrl.isEmpty()) {
                System.err.println("where env.DB?");
                return;
            }
            DBManager.init(dbUrl);
            vehicleRepository = new JDBCVehicleRepository();
            userRepository = new JDBCUserRepository();
            rentalRepository = new JDBCRentalRepository();
            System.out.println("using JDBC...");
        } else {
            vehicleRepository = new VehicleRepositoryImpl();
            userRepository = new UserRepositoryImpl();
            rentalRepository = new RentalRepositoryImpl();
            System.out.println("using JSON...");
        }

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
