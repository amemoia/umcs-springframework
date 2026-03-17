package cwiczenia;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        VehicleRepositoryImpl repo = new VehicleRepositoryImpl();
        Scanner sc = new Scanner(System.in);

        System.out.println("\n\nWypozyczalnia samochodow");

        while (true) {

            System.out.println("\nWybierz opcje wpisujac numer:");
            System.out.println("    1. Wyswietl auta w bazie");
            System.out.println("    2. Wypozycz samochod");
            System.out.println("    3. Zwroc samochod");

            String input = sc.nextLine();
            if (input.equals("1")) {
                List<Vehicle> vehicles = repo.getVehicles();
                for (Vehicle v : vehicles) {
                    System.out.println(v);
                }
            }

            else if (input.equals("2")) {
                List<Vehicle> vehicles = repo.getVehicles();
                for (Vehicle v : vehicles) {
                    if (!v.isRented()) System.out.println(v);
                }

                System.out.println("Wpisz [ID] auta, ktore chcesz wypozyczyc");
                String id = sc.nextLine();
                for (Vehicle v : vehicles) {
                    if (v.id.equals(id)) repo.rentVehicle(v);
                }
            }

            else if (input.equals("3")) {
                List<Vehicle> vehicles = repo.getVehicles();
                for (Vehicle v : vehicles) {
                    if (v.isRented()) System.out.println(v);
                }

                System.out.println("Wpisz [ID] auta, ktore zostalo zwrocone");
                String id = sc.nextLine();
                for (Vehicle v : vehicles) {
                    if (v.id.equals(id)) repo.returnVehicle(v);
                }
            }
        }
    }
}