package com.umcsuser.carrent;

import com.umcsuser.carrent.models.Car;
import com.umcsuser.carrent.models.Motorcycle;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public final class HibernateUtil {
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        String dbUrl = System.getenv("DB");
        if (dbUrl == null || dbUrl.isBlank()) {
            throw new IllegalStateException("need env.DB for hibernate");
        }

        Properties props = new Properties();
        props.put("hibernate.connection.url", dbUrl);

        String dbUser = System.getenv("DB_USER");
        if (dbUser != null && !dbUser.isBlank()) {
            props.put("hibernate.connection.username", dbUser);
        }

        String dbPass = System.getenv("DB_PASS");
        if (dbPass != null && !dbPass.isBlank()) {
            props.put("hibernate.connection.password", dbPass);
        }

        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "false");

        Configuration configuration = new Configuration();
        configuration.setProperties(props);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Vehicle.class);
        configuration.addAnnotatedClass(Car.class);
        configuration.addAnnotatedClass(Motorcycle.class);
        configuration.addAnnotatedClass(Rental.class);

        return configuration.buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        SESSION_FACTORY.close();
    }
}