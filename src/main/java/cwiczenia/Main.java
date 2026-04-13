package cwiczenia;

import java.util.List;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        IVehicleRepository vehicleRepository = new VehicleRepositoryImpl();
        UserRepository userRepository = new UserRepositoryImpl();
        IRentalRepository rentalRepository = new RentalRepositoryImpl();
        AuthService authService = new AuthService(userRepository);
        RentalService rentalService = new RentalService(vehicleRepository, userRepository, rentalRepository);

        Scanner sc = new Scanner(System.in);

        System.out.println("\n\nWypozyczalnia samochodow");

        User curUser = null;
        while (curUser == null) {
            System.out.println("1. Zaloguj sie");
            System.out.println("2. Zarejestruj sie");
            String input = sc.nextLine();
            if (input.equals("1")) {
                System.out.println("login:");
                String login = sc.nextLine();
                System.out.println("haslo:");
                String password = sc.nextLine();

                curUser = authService.login(login, password);

                if (curUser == null) {
                    System.out.println("!!! niepoprawny login lub haslo");
                } else {
                    System.out.println("\nzalogowano jako " + curUser.getRole() + " : " + curUser.getLogin());
                }
            }
            if (input.equals("2")) {
                System.out.println("login:");
                String login = sc.nextLine();
                System.out.println("haslo:");
                String password = sc.nextLine();
                String rola = "USER";
                curUser = authService.register(login, password, rola);
                if (curUser == null) {
                    System.out.println("!!! uzytkownik z tym loginem juz istnieje");
                } else {
                    userRepository.save();
                    System.out.println("\nzarejestrowano i zalogowano jako " + curUser.getRole() + " : " + curUser.getLogin());
                }
            }
        }

        boolean logout = false;
        while (!logout) {
            if (curUser.getRole().equalsIgnoreCase("admin")) {
                System.out.println("\n    Menu admina");
                System.out.println("1. Przegladaj pojazdy");
                System.out.println("2. Dodaj pojazd");
                System.out.println("3. Usun pojazd");
                System.out.println("4. Lista uzytkownikow");
                System.out.println("5. Dodaj uzytkownika");
                System.out.println("6. Usun uzytkownika");
                System.out.println("0. Wyloguj sie");

                String input = sc.nextLine();

                if (input.equals("1")) {
                    System.out.println("\n    Wszystkie pojazdy");
                    List<Vehicle> vehicles = vehicleRepository.getVehicles();
                    for (Vehicle v : vehicles) {
                        System.out.println(v);
                    }
                } else if (input.equals("2")) {
                    System.out.println("\n    Dodawanie pojazdu");
                    System.out.println("typ (CAR/MOTORCYCLE):");
                    String type = sc.nextLine();
                    System.out.println("ID:");
                    String id = sc.nextLine();
                    System.out.println("marka:");
                    String brand = sc.nextLine();
                    System.out.println("model:");
                    String model = sc.nextLine();
                    System.out.println("rok:");
                    int year = Integer.parseInt(sc.nextLine());
                    System.out.println("cena:");
                    float price = Float.parseFloat(sc.nextLine());

                    Vehicle vehicle = null;
                    if (type.equalsIgnoreCase("CAR")) {
                        vehicle = new Car(id, brand, model, year, price, false);
                    } else if (type.equalsIgnoreCase("MOTORCYCLE")) {
                        System.out.println("Kategoria:");
                        String kategoria = sc.nextLine();
                        MotorcycleCategory cat = null;
                        while (cat == null) {
                            try {
                                cat = MotorcycleCategory.valueOf(kategoria);
                            } catch (IllegalArgumentException e) {
                                System.out.println("!!! niepoprawna kategoria: " + kategoria);
                            }
                        }
                        vehicle = new Motorcycle(id, brand, model, year, price, false, cat);
                    }
                    if (vehicle != null) {
                        vehicleRepository.add(vehicle);
                        vehicleRepository.save();
                        System.out.println("Pojazd dodany!");
                    }
                } else if (input.equals("3")) {
                    System.out.println("\n    Wszystkie pojazdy");
                    List<Vehicle> vehicles = vehicleRepository.getVehicles();
                    int counter = 1;
                    for (Vehicle v : vehicles) {
                        System.out.println(counter + ". " + v);
                        counter++;
                    }
                    System.out.println("Numer pojazdu do usuniecia (lub 0 aby anulowac):");
                    String numStr = sc.nextLine();
                    try {
                        int num = Integer.parseInt(numStr);
                        if (num == 0) {
                            System.out.println("Anulowano");
                        } else if (num > 0 && num <= vehicles.size()) {
                            Vehicle v = vehicles.get(num - 1);
                            if (v != null && !v.isRented()) {
                                vehicleRepository.remove(v.getId());
                                vehicleRepository.save();
                                System.out.println("usunieto pojazd");
                            } else if (v != null && v.isRented()) {
                                System.out.println("pojazd jest wynajety - nie mozna go usunac");
                            }
                        } else {
                            System.out.println("Zly numer pojazdu");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Niepoprawny numer");
                    }
                } else if (input.equals("4")) {
                    System.out.println("\n    Lista uzytkownikow");
                    List<User> users = userRepository.getUsers();
                    for (User u : users) {
                        System.out.println("login: " + u.getLogin() + " (" + u.getRole() + ")");
                        if (u.getRentedVehicleId() != null) {
                            Vehicle v = vehicleRepository.getVehicle(u.getRentedVehicleId());
                            System.out.println("  pojazd: " + v);
                        } else {
                            System.out.println("  pojazd: brak");
                        }
                    }
                } else if (input.equals("5")) {
                    System.out.println("login:");
                    String login = sc.nextLine();
                    System.out.println("haslo:");
                    String password = sc.nextLine();
                    System.out.println("rola (USER/ADMIN):");
                    String rola = sc.nextLine();
                    while (!rola.equalsIgnoreCase("USER") && !rola.equalsIgnoreCase("ADMIN")) {
                        System.out.println("try again - rola (USER/ADMIN):");
                        rola = sc.nextLine();
                    }
                    User newUser = authService.register(login, password, rola);
                    if (newUser == null) {
                        System.out.println("!!! blad w rejestracji - uzytkownik juz istnieje");
                    } else {
                        userRepository.save();
                        System.out.println("\nzarejestrowano uzytkownika " + newUser.getRole() + " : " + newUser.getLogin());
                    }
                } else if (input.equals("6")) {
                    System.out.println("\n    Lista uzytkownikow");
                    List<User> users = userRepository.getUsers();
                    for (User u : users) {
                        System.out.println("login: " + u.getLogin() + " (" + u.getRole() + ")");
                        if (u.getRentedVehicleId() != null) {
                            Vehicle v = vehicleRepository.getVehicle(u.getRentedVehicleId());
                            System.out.println("  pojazd: " + v);
                        } else {
                            System.out.println("  pojazd: brak");
                        }
                    }

                    System.out.println("Wpisz login uzytkownika do usuniecia: ");
                    String toRemove = sc.nextLine();
                    User check = userRepository.getUser(toRemove);
                    if (check != null) {
                        // podaje admin jako argument w celu autoryzacji
                        int code = userRepository.removeUser(toRemove, curUser);
                        if (code == 0) {
                            System.out.println("Usunieto uzytkownika " + toRemove);
                            userRepository.save();
                        }
                        else if (code == 1) {
                            System.out.println("Nie masz uprawnien do usuwania uzytkownikow");
                        }
                        else if (code == 2) {
                            System.out.println("Nie mozna usunac tego uzytkownika - ma wypozyczone auto");
                        }
                    }
                    else {
                        System.out.println("Nie znaleziono takiego usera");
                    }
                } else if (input.equals("0")) {
                    System.out.println("!!! wylogowano");
                    logout = true;
                }
            } else {
                System.out.println("\n    Menu");
                System.out.println("1. Wyswietl dostepne pojazdy");
                System.out.println("2. Wypozycz pojazd");
                System.out.println("3. Zwroc pojazd");
                System.out.println("4. Wyloguj sie");

                String input = sc.nextLine();

                if (input.equals("1")) {
                    System.out.println("\n    Dostepne pojazdy");
                    List<Vehicle> vehicles = vehicleRepository.getVehicles();
                    int counter = 1;
                    for (Vehicle v : vehicles) {
                        if (!v.isRented()) {
                            System.out.println(counter + ". " + v);
                            counter++;
                        }
                    }
                } else if (input.equals("2")) {
                    if (curUser.getRentedVehicleId() == null) {
                        System.out.println("\n    Dostepne pojazdy");
                        List<Vehicle> vehicles = vehicleRepository.getVehicles();
                        List<Vehicle> available = new java.util.ArrayList<>();
                        int counter = 1;
                        for (Vehicle v : vehicles) {
                            if (!v.isRented()) {
                                System.out.println(counter + ". " + v);
                                available.add(v);
                                counter++;
                            }
                        }
                        if (available.isEmpty()) {
                            System.out.println("Brak dostepnych pojazdow");
                        } else {
                            System.out.println("Podaj numer pojazdu (lub 0 aby anulowac):");
                            String numStr = sc.nextLine();
                            try {
                                int num = Integer.parseInt(numStr);
                                if (num == 0) {
                                    System.out.println("Anulowano");
                                } else if (num > 0 && num <= available.size()) {
                                    Vehicle v = available.get(num - 1);
                                    Rental rental = rentalService.rentVehicle(curUser.getLogin(), v.getId());
                                    if (rental != null) {
                                        vehicleRepository.save();
                                        userRepository.save();
                                        rentalRepository.save();
                                        curUser = userRepository.getUser(curUser.getLogin());
                                    }
                                } else {
                                    System.out.println("Zly numer pojazdu");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Niepoprawny numer");
                            }
                        }
                    } else {
                        System.out.println("!!! masz juz wypozyczony pojazd");
                    }
                } else if (input.equals("3")) {
                    if (curUser.getRentedVehicleId() != null) {
                        Rental rental = rentalService.returnVehicle(curUser.getLogin(), curUser.getRentedVehicleId());
                        if (rental != null) {
                            vehicleRepository.save();
                            userRepository.save();
                            rentalRepository.save();
                            curUser = userRepository.getUser(curUser.getLogin());
                        }
                    } else {
                        System.out.println("!!! nie masz wypozyczonego pojazdu");
                    }
                } else if (input.equals("4")) {
                    System.out.println("!!! wylogowano");
                    logout = true;
                }
            }
        }
        
        sc.close();
    }
}

