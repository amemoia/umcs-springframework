package com.umcsuser.carrent;

import com.umcsuser.carrent.models.*;
import com.umcsuser.carrent.services.*;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UI {

    private final AuthService authService;
    private final VehicleService vehicleService;
    private final RentalService rentalService;
    private final UserService userService;
    private final VehicleCategoryConfigService categoryConfigService;
    private final Scanner scanner = new Scanner(System.in);

    public UI(AuthService authService, VehicleService vehicleService, RentalService rentalService,
              UserService userService, VehicleCategoryConfigService categoryConfigService) {
        this.authService = authService;
        this.vehicleService = vehicleService;
        this.rentalService = rentalService;
        this.userService = userService;
        this.categoryConfigService = categoryConfigService;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== START ===\n1. Zaloguj\n2. Zarejestruj\n0. Koniec");
            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    User loggedUser = login();
                    if (loggedUser != null) {
                        System.out.println("Zalogowano: " + loggedUser.getLogin() + " (" + loggedUser.getRole() + ")");
                        if (loggedUser.getRole() == Role.ADMIN) adminMenu(loggedUser);
                        else userMenu(loggedUser);
                    } else {
                        System.out.println("Nieprawidłowy login lub hasło.");
                    }
                }
                case "2" -> register();
                case "0" -> { return; }
                default -> System.out.println("Nieprawidłowa opcja.");
            }
        }
    }

    private void register() {
        System.out.println("=== Rejestracja ===");
        if (authService.register(readText("Podaj login: "), readText("Podaj hasło: "), "USER")) {
            System.out.println("Zarejestrowano pomyślnie.");
        } else {
            System.out.println("Błąd rejestracji. Użytkownik prawdopodobnie już istnieje.");
        }
    }

    private User login() {
        System.out.println("=== Logowanie ===");
        return authService.login(readText("Login: "), readText("Hasło: "));
    }

    private void adminMenu(User loggedUser) {
        while (true) {
            System.out.println("\n=== MENU ADMINA ===");
            System.out.println("1. Pokaż pojazdy | 2. Dodaj pojazd | 3. Usuń pojazd | 4. Pokaż użytkowników | 5. Usuń użytkownika | 6. Moje dane | 7. Historia wypożyczeń | 0. Wyloguj");

            switch (scanner.nextLine().trim()) {
                case "1" -> vehicleService.findAllVehicles().forEach(v ->
                        System.out.println(v.toString() +
                                " [Wypożyczony: " + vehicleService.isVehicleRented(v.getId()) + "]"
                                + "[Wypożyczony: " +rentalService.vehicleHasActiveRental(v.getId()) + "]"));
                case "2" -> addVehicle();
                case "3" -> deleteVehicle();
                case "4" -> showAllUsers();
                case "5" -> deleteUser(loggedUser);
                case "6" -> showCurrentUserData(loggedUser);
                case "7" -> showRentalHistory();
                case "0" -> { return; }
                default -> System.out.println("Nieprawidłowa opcja.");
            }
        }
    }

    private void userMenu(User loggedUser) {
        while (true) {
            System.out.println("\n=== MENU USERA ===");
            System.out.println("1. Dostępne pojazdy | 2. Wypożycz | 3. Zwróć | 4. Moje dane | 5. Moja historia | 0. Wyloguj");

            switch (scanner.nextLine().trim()) {
                case "1" -> vehicleService.findAvailableVehicles().forEach(v -> System.out.println(v.toString()));
                case "2" -> rentVehicle(loggedUser);
                case "3" -> returnVehicle(loggedUser);
                case "4" -> showCurrentUserData(loggedUser);
                case "5" -> {
                    List<Rental> rentals = rentalService.findUserRentals(loggedUser.getId());
                    if (rentals.isEmpty()) System.out.println("Brak historii wypożyczeń.");
                    else rentals.forEach(this::printRentalDetails);
                }
                case "0" -> { return; }
                default -> System.out.println("Nieprawidłowa opcja.");
            }
        }
    }

    private void addVehicle() {
        System.out.println("=== Dodawanie pojazdu ===");
        List<VehicleCategoryConfig> categories = categoryConfigService.findAllCategories();
        if (categories.isEmpty()) {
            System.out.println("Brak skonfigurowanych kategorii pojazdów.");
            return;
        }

        System.out.println("Dostępne kategorie:");
        categories.forEach(c -> System.out.println("- " + c.getCategory()));

        try {
            VehicleCategoryConfig config = categoryConfigService.getByCategory(readText("Podaj kategorię: "));
            Vehicle vehicle = Vehicle.builder()
                    .category(config.getCategory())
                    .brand(readText("Marka: "))
                    .model(readText("Model: "))
                    .year(readInt("Rok: "))
                    .plate(readText("Rejestracja: "))
                    .price(readDouble("Cena: "))
                    .build();

            for (Map.Entry<String, String> entry : config.getAttributes().entrySet()) {
                vehicle.addAttribute(entry.getKey(), readAttributeValue(entry.getKey(), entry.getValue()));
            }

            System.out.println("Dodano pojazd o ID: " + vehicleService.addVehicle(vehicle).getId());
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void deleteVehicle() {
        try {
            vehicleService.removeVehicle(readText("ID pojazdu do usunięcia: "));
            System.out.println("Usunięto pomyślnie.");
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void deleteUser(User loggedUser) {
        try {
            userService.deleteUser(readText("ID użytkownika do usunięcia: "), loggedUser.getId());
            System.out.println("Użytkownik usunięty.");
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void showAllUsers() {
        List<User> users = userService.findAllUsers();
        if (users.isEmpty()) {
            System.out.println("Brak użytkowników.");
            return;
        }

        users.forEach(user -> {
            System.out.println(user);
            List<Rental> rentals = rentalService.findUserRentals(user.getId());

            if (rentals.isEmpty()) {
                System.out.println("  Historia wypożyczeń: brak\n--------------------");
            } else {
                System.out.println("  Historia wypożyczeń:");
                rentals.forEach(this::printRentalDetails);
            }
        });
    }

    private void rentVehicle(User loggedUser) {
        try {
            rentalService.rentVehicle(loggedUser.getId(), readText("ID pojazdu do wypożyczenia: "));
            System.out.println("Pojazd został wypożyczony.");
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void returnVehicle(User loggedUser) {
        try {
            rentalService.returnVehicle(loggedUser.getId());
            System.out.println("Pojazd został zwrócony.");
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void showCurrentUserData(User loggedUser) {
        try {
            User u = userService.findById(loggedUser.getId());
            System.out.println("ID: " + u.getId() + " | Login: " + u.getLogin() + " | Rola: " + u.getRole());

            rentalService.findActiveRentalByUserId(u.getId())
                    .ifPresentOrElse(
                            rental -> {
                                try {
                                    System.out.println("Aktualnie wypożyczony pojazd: " + vehicleService.findById(rental.getVehicleId()));
                                } catch (Exception e) {
                                    System.out.println("Aktualnie wypożyczony pojazd: " + rental.getVehicleId() + " (brak szczegółów)");
                                }
                            },
                            () -> System.out.println("Brak aktywnego wypożyczenia.")
                    );
        } catch (Exception e) {
            System.out.println("Nie udało się odczytać danych użytkownika.");
        }
    }

    private void showRentalHistory() {
        List<Rental> rentals = rentalService.findAllRentals();
        if (rentals.isEmpty()) {
            System.out.println("Brak historii wypożyczeń.");
            return;
        }
        rentals.forEach(this::printRentalDetails);
    }

    private String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("To pole nie może być puste!");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readText(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Wpisz poprawną liczbę całkowitą!");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            try {
                double val = Double.parseDouble(readText(prompt));
                if (val >= 0) return val;
                System.out.println("Wartość nie może być ujemna!");
            } catch (NumberFormatException e) {
                System.out.println("Wpisz poprawną liczbę!");
            }
        }
    }

    private boolean readBoolean(String prompt) {
        while (true) {
            String input = readText(prompt).toLowerCase();
            if (input.equals("true")) return true;
            if (input.equals("false")) return false;
            System.out.println("Wpisz 'true' lub 'false'.");
        }
    }

    private Object readAttributeValue(String attrName, String attrType) {
        return switch (attrType.toLowerCase()) {
            case "string" -> readText(attrName + " (tekst): ");
            case "number" -> readDouble(attrName + " (liczba): ");
            case "boolean" -> readBoolean(attrName + " (true/false): ");
            case "integer" -> readInt(attrName + " (liczba calkowita): ");
            default -> throw new IllegalArgumentException("Nieznany typ: " + attrType);
        };
    }

    private void printRentalDetails(Rental rental) {
        System.out.println(rental);

        String login = "nieznany";
        try {
            login = userService.findById(rental.getUserId()).getLogin();
        } catch (Exception ignored) {}

        String vehicle = "nieznany";
        try {
            vehicle = vehicleService.findById(rental.getVehicleId()).toString();
        } catch (Exception ignored) {}

        System.out.println("  user: " + login);
        System.out.println("  vehicle: " + vehicle);
        System.out.println("--------------------");
    }
}