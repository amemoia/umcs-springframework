package com.umcsuser.carrent.repositories.impl;

import com.google.gson.*;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RentalRepositoryImpl implements RentalRepository {
    private Map<String, Rental> rentals = new HashMap<>();
    private File jsonFile = new File("rentals.json");
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public RentalRepositoryImpl() {
        load();
    }

    @Override
    public List<Rental> findAll() {
        return new ArrayList<>(rentals.values());
    }

    @Override
    public Optional<Rental> findById(String id) {
        return Optional.ofNullable(rentals.get(id));
    }

    @Override
    public Rental save(Rental rental) {
        rentals.put(rental.getId(), rental);
        saveToFile();
        return rental;
    }

    @Override
    public void deleteById(String id) {
        rentals.remove(id);
        saveToFile();
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        return rentals.values().stream()
                .filter(r -> r.getVehicleId().equals(vehicleId) && r.isActive())
                .findFirst();
    }

    private void saveToFile() {
        try {
            JsonArray array = new JsonArray();
            for (Rental r : rentals.values()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", r.getId());
                obj.addProperty("vehicleId", r.getVehicleId());
                obj.addProperty("userId", r.getUserId());
                obj.addProperty("rentDateTime", r.getRentDateTime());
                obj.addProperty("returnDateTime", r.getReturnDateTime());
                array.add(obj);
            }
            
            String json = gson.toJson(array);
            Files.write(Paths.get(jsonFile.getPath()), json.getBytes());
            System.out.println("rental repo save successful");
        } catch (IOException e) {
            System.out.println("error saving rentals!");
            e.printStackTrace();
        }
    }

    private void load() {
        try {
            if (!jsonFile.exists()) {
                System.out.println("rentals.json not found");
                return;
            }

            String json = new String(Files.readAllBytes(Paths.get(jsonFile.getPath())));
            JsonArray array = gson.fromJson(json, JsonArray.class);
            
            if (array != null) {
                for (JsonElement element : array) {
                    JsonObject obj = element.getAsJsonObject();
                    
                    String id = obj.get("id").getAsString();
                    String vehicleId = obj.get("vehicleId").getAsString();
                    String userId = obj.get("userId").getAsString();
                    String rentDateTime = obj.get("rentDateTime").getAsString();
                    String returnDateTime = obj.has("returnDateTime") && !obj.get("returnDateTime").isJsonNull() 
                        ? obj.get("returnDateTime").getAsString() 
                        : null;
                    
                    Rental rental = new Rental(id, vehicleId, userId, rentDateTime, returnDateTime);
                    rentals.put(id, rental);
                }
            }
        } catch (IOException e) {
            System.out.println("error loading rental");
            e.printStackTrace();
        }
    }
}
