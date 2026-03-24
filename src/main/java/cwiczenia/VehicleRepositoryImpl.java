package cwiczenia;

import java.io.*;
import java.util.*;

public class VehicleRepositoryImpl implements IVehicleRepository {
    private Map<String, Vehicle> vehicles = new HashMap<>();
    private File csv = new File("vehicles.csv");

    public VehicleRepositoryImpl() {
        load();
    }

    @Override
    public boolean rentVehicle(String id) {
        Vehicle v = getVehicle(id);
        if (v.isRented()) { System.out.println("cannot rent this vehicle - already taken"); return false; }
        else {
            System.out.println("renting: " + v);
            v.setRented(true);
            vehicles.put(v.id, v);
            return true;
        }
    }

    @Override
    public boolean returnVehicle(String id) {
        Vehicle v = getVehicle(id);
        if (!v.isRented()) { System.out.println("cannot return this vehicle - it's not rented"); return false; }
        else {
            System.out.println("returning: " + v);
            v.setRented(false);
            vehicles.put(v.id, v);
            return true;
        }
    }

    @Override
    public List<Vehicle> getVehicles() {
        List<Vehicle> copy = new ArrayList<>();
        for (Vehicle v : vehicles.values()) {
            copy.add(v.copy());
        }
        return copy;
    }

    @Override
    public Vehicle getVehicle(String id) {
        return vehicles.get(id);
    }

    @Override
    public boolean add(Vehicle vehicle) {
        vehicles.put(vehicle.id, vehicle);
        return true;
    }

    @Override
    public boolean remove(String id) {
        vehicles.remove(id);
        return true;
    }

    @Override
    public void save() {
        //w3schools
        try {
            PrintWriter myWriter = new PrintWriter(csv);
            //myWriter.write("Files in Java might be tricky, but it is fun enough!");
            for (Vehicle v : vehicles.values()) myWriter.println(v.toCSV());
            myWriter.close();  // must close manually
            System.out.println("vehicle repo save successful");
        } catch (IOException e) {
            System.out.println("error!");
            e.printStackTrace();
        }
    }

    public void load() {
        // w3schools
        try (Scanner myReader = new Scanner(csv)) {
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] split = line.split(";");

                String type = split[0];
                Vehicle cur;
                if (type.equals("MOTORCYCLE")) {
                    cur = new Motorcycle(split[1], split[2], split[3], Integer.parseInt(split[4]), Float.parseFloat(split[5]), Boolean.parseBoolean(split[6]), MotorcycleCategory.valueOf(split[7]));
                    vehicles.put(cur.id, cur);
                }
                else if (type.equals("CAR")) {
                    cur = new Car(split[1], split[2], split[3], Integer.parseInt(split[4]), Float.parseFloat(split[5]), Boolean.parseBoolean(split[6]));
                    vehicles.put(cur.id, cur);
                }

                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("error!");
            e.printStackTrace();
        }
    }
}

