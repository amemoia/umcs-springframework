package com.umcsuser.carrent.repositories.impl;

import com.google.gson.*;
import com.umcsuser.carrent.models.Car;
import com.umcsuser.carrent.models.Motorcycle;
import com.umcsuser.carrent.models.MotorcycleCategory;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class VehicleRepositoryImpl implements VehicleRepository {
    private Map<String, Vehicle> vehicles = new HashMap<>();
    private File jsonFile = new File("vehicles.json");
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public VehicleRepositoryImpl() {
        load();
    }

    @Override
    public List<Vehicle> getVehicles() {
        return new ArrayList<>(vehicles.values());
    }

    @Override
    public Vehicle getVehicle(String id) {
        return vehicles.get(id);
    }

    @Override
    public Vehicle add(Vehicle vehicle) {
        vehicles.put(vehicle.getId(), vehicle);
        save();
        return vehicle;
    }

    @Override
    public Vehicle update(Vehicle vehicle) {
        vehicles.put(vehicle.getId(), vehicle);
        save();
        return vehicle;
    }

    @Override
    public boolean remove(String id) {
        if (vehicles.containsKey(id)) {
            vehicles.remove(id);
            save();
            return true;
        }
        return false;
    }

    @Override
    public void save() {
        try {
            JsonArray array = new JsonArray();
            for (Vehicle v : vehicles.values()) {
                JsonObject obj = new JsonObject();
                if (v instanceof Car) {
                    obj.addProperty("type", "CAR");
                } else if (v instanceof Motorcycle) {
                    obj.addProperty("type", "MOTORCYCLE");
                    obj.addProperty("category", ((Motorcycle) v).getKategoria() != null ? ((Motorcycle) v).getKategoria().toString() : "null");
                }
                obj.addProperty("id", v.getId());
                obj.addProperty("brand", v.getBrand());
                obj.addProperty("model", v.getModel());
                obj.addProperty("year", v.getYear());
                obj.addProperty("price", v.getPrice());
                obj.addProperty("rented", v.isRented());
                array.add(obj);
            }
            
            String json = gson.toJson(array);
            Files.write(Paths.get(jsonFile.getPath()), json.getBytes());
            System.out.println("vehicle repo save successful");
        } catch (IOException e) {
            System.out.println("error saving vehicles!");
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        try {
            if (!jsonFile.exists()) {
                System.out.println("vehicles.json not found, starting with empty vehicles");
                return;
            }

            String json = new String(Files.readAllBytes(Paths.get(jsonFile.getPath())));
            JsonArray array = gson.fromJson(json, JsonArray.class);
            
            if (array != null) {
                for (JsonElement element : array) {
                    JsonObject obj = element.getAsJsonObject();
                    
                    // Bezpieczne odczytywanie pól
                    if (!obj.has("id") || !obj.has("brand") || !obj.has("model") || 
                        !obj.has("year") || !obj.has("price")) {
                        System.out.println("Skipping invalid vehicle entry: missing required fields");
                        continue;
                    }
                    
                    String id = obj.get("id").getAsString();
                    String brand = obj.get("brand").getAsString();
                    String model = obj.get("model").getAsString();
                    int year = obj.get("year").getAsInt();
                    float price = obj.get("price").getAsFloat();
                    boolean rented = obj.has("rented") ? obj.get("rented").getAsBoolean() : false;
                    
                    // Obsługa pola "category" zamiast "type"
                    String category = obj.has("category") ? obj.get("category").getAsString() : null;
                    if (category == null && obj.has("type")) {
                        category = obj.get("type").getAsString();
                    }
                    
                    if (category == null) {
                        System.out.println("Skipping vehicle without type/category");
                        continue;
                    }
                    
                    Vehicle vehicle;
                    if (category.equalsIgnoreCase("CAR")) {
                        vehicle = new Car(id, brand, model, year, price, rented);
                    } else if (category.equalsIgnoreCase("MOTORCYCLE")) {
                        MotorcycleCategory cat = MotorcycleCategory.A; // Domyślna kategoria
                        
                        // Spróbuj pobrać kategorię
                        if (obj.has("category")) {
                            String licence = obj.get("category").getAsString();
                            try {
                                cat = MotorcycleCategory.valueOf(licence);
                            } catch (IllegalArgumentException e) {
                                System.out.println("Invalid motorcycle category: " + licence);
                            }
                        }
                        vehicle = new Motorcycle(id, brand, model, year, price, rented, cat);
                    } else {
                        System.out.println("Skipping unknown vehicle type: " + category);
                        continue;
                    }
                    
                    vehicles.put(id, vehicle);
                }
            }
        } catch (IOException e) {
            System.out.println("error loading vehicles!");
            e.printStackTrace();
        }
    }
}
