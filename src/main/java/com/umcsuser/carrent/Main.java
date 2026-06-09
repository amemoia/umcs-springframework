package com.umcsuser.carrent;

import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleCategoryConfigRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.repositories.impl.RentalRepositoryImpl;
import com.umcsuser.carrent.repositories.impl.UserRepositoryImpl;
import com.umcsuser.carrent.repositories.impl.VehicleCategoryConfigJsonRepository;
import com.umcsuser.carrent.repositories.impl.VehicleRepositoryImpl;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.core.io.DefaultResourceLoader;

import javax.sql.DataSource;

import com.umcsuser.carrent.services.AuthService;
import com.umcsuser.carrent.services.RentalService;
import com.umcsuser.carrent.services.UserService;
import com.umcsuser.carrent.services.VehicleCategoryConfigService;
import com.umcsuser.carrent.services.VehicleService;
import com.umcsuser.carrent.services.VehicleValidator;
import com.umcsuser.carrent.repositories.impl.*;

public class Main {
    public static void main(String[] args) {
        if (shouldStartWeb(args)) {
            SpringApplication.run(CarrentApplication.class, args);
            return;
        }
        String repoType = "hibernate";

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("json") || args[0].equalsIgnoreCase("jdbc") || args[0].equalsIgnoreCase("hibernate")) {
                repoType = args[0].toLowerCase();
            }
        }

        VehicleRepository vehicleRepository;
        UserRepository userRepository;
        RentalRepository rentalRepository;
        VehicleCategoryConfigRepository categoryConfigRepository = new VehicleCategoryConfigJsonRepository("categories.json", new DefaultResourceLoader());

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
            DataSource dataSource = buildDataSource(dbUrl);
            vehicleRepository = new JDBCVehicleRepository(dataSource);
            userRepository = new JDBCUserRepository(dataSource);
            rentalRepository = new JDBCRentalRepository(dataSource);
            System.out.println("using JDBC...");
        } else {
            vehicleRepository = new VehicleRepositoryImpl("vehicles.json");
            userRepository = new UserRepositoryImpl("users.json");
            rentalRepository = new RentalRepositoryImpl("rentals.json");
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

    private static DataSource buildDataSource(String dbUrl) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(dbUrl);
        return dataSource;
    }

    private static boolean shouldStartWeb(String[] args) {
        String mode = System.getenv("APP_MODE");
        if (mode != null && mode.equalsIgnoreCase("cli")) {
            return false;
        }
        if (mode != null && mode.equalsIgnoreCase("web")) {
            return true;
        }
        String profile = System.getenv("APP_PROFILE");
        if (profile != null && !profile.isBlank()) {
            return true;
        }
        for (String arg : args) {
            if ("web".equalsIgnoreCase(arg)) {
                return true;
            }
        }
        return false;
    }
}
