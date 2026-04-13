package cwiczenia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RentalRepositoryImpl implements IRentalRepository {
    private Map<String, Rental> rentals = new HashMap<>();
    private File jsonFile = new File("rentals.json");
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public RentalRepositoryImpl() {
        load();
    }

    @Override
    public List<Rental> getRentals() {
        List<Rental> copy = new ArrayList<>();
        for (Rental r : rentals.values()) {
            copy.add(r.copy());
        }
        return copy;
    }

    @Override
    public Rental getRental(String id) {
        Rental r = rentals.get(id);
        return r != null ? r.copy() : null;
    }

    @Override
    public Rental add(Rental rental) {
        rentals.put(rental.getId(), rental);
        return rental;
    }

    @Override
    public Rental update(Rental rental) {
        rentals.put(rental.getId(), rental);
        return rental;
    }

    @Override
    public boolean remove(String id) {
        rentals.remove(id);
        return true;
    }

    @Override
    public Rental findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        for (Rental r : rentals.values()) {
            if (r.getVehicleId().equals(vehicleId) && r.isActive()) {
                return r.copy();
            }
        }
        return null;
    }

    @Override
    public List<Rental> findByUserId(String userId) {
        List<Rental> result = new ArrayList<>();
        for (Rental r : rentals.values()) {
            if (r.getUserId().equals(userId)) {
                result.add(r.copy());
            }
        }
        return result;
    }

    @Override
    public List<Rental> findActiveRentals() {
        List<Rental> result = new ArrayList<>();
        for (Rental r : rentals.values()) {
            if (r.isActive()) {
                result.add(r.copy());
            }
        }
        return result;
    }

    @Override
    public void save() {
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

    @Override
    public void load() {
        try {
            if (!jsonFile.exists()) {
                System.out.println("rentals.json not found, starting with empty rentals");
                return;
            }

            String json = new String(Files.readAllBytes(Paths.get(jsonFile.getPath())));
            JsonArray array = gson.fromJson(json, JsonArray.class);
            
            if (array != null) {
                for (JsonElement element : array) {
                    JsonObject obj = element.getAsJsonObject();
                    
                    // Bezpieczne odczytywanie pól
                    if (!obj.has("id") || !obj.has("vehicleId") || !obj.has("userId") || !obj.has("rentDateTime")) {
                        System.out.println("Skipping invalid rental entry: missing required fields");
                        continue;
                    }
                    
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
            System.out.println("error loading rentals!");
            e.printStackTrace();
        }
    }
}
