package cwiczenia;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VehicleRepositoryImpl implements IVehicleRepository {
    private List<Vehicle> vehicles = new ArrayList<>();
    private File csv = new File("vehicles.txt");

    public VehicleRepositoryImpl() {
        load();
    }

    @Override
    public void rentVehicle(Vehicle v) {
        if (v.isRented()) System.out.println("cannot rent this vehicle - already taken");
        else {
            System.out.println("renting: " + v);
            v.setRented(true);
            for (int i = 0; i < vehicles.size(); i++) {
                if (vehicles.get(i).id.equals(v.id)) {
                    vehicles.set(i, v);
                    break;
                }
            }
        }
    }

    @Override
    public void returnVehicle(Vehicle v) {
        if (!v.isRented()) System.out.println("cannot return this vehicle - it's not rented");
        else {
            System.out.println("returning: " + v);
            v.setRented(false);
            for (int i = 0; i < vehicles.size(); i++) {
                if (vehicles.get(i).id.equals(v.id)) {
                    vehicles.set(i, v);
                    break;
                }
            }
        }
    }

    @Override
    public List<Vehicle> getVehicles() {
        List<Vehicle> copy = new ArrayList<Vehicle>();
        for (Vehicle v : vehicles) {
            copy.add(v.copy());
        }
        return copy;
    }

    @Override
    public void save() {
        //w3schools
        try {
            PrintWriter myWriter = new PrintWriter(csv);
            //myWriter.write("Files in Java might be tricky, but it is fun enough!");
            for (Vehicle v : vehicles) myWriter.println(v.toCSV());
            myWriter.close();  // must close manually
            System.out.println("save successful");
        } catch (IOException e) {
            System.out.println("error!");
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        // w3schools
        try (Scanner myReader = new Scanner(csv)) {
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] split = line.split(";");

                String type = split[0];
                Vehicle cur;
                if (type.equals("MOTORCYCLE")) {
                    cur = new Motorcycle(split[1], split[2], split[3], Integer.parseInt(split[4]), Float.parseFloat(split[5]), Boolean.parseBoolean(split[6]), split[7]);
                    vehicles.add(cur);
                }
                else if (type.equals("CAR")) {
                    cur = new Car(split[1], split[2], split[3], Integer.parseInt(split[4]), Float.parseFloat(split[5]), Boolean.parseBoolean(split[6]));
                    vehicles.add(cur);
                }

                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("error!");
            e.printStackTrace();
        }
    }
}

