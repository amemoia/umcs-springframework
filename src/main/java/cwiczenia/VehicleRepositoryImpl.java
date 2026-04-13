package cwiczenia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class VehicleRepositoryImpl implements IVehicleRepository {
    private Map<String, Vehicle> vehicles = new HashMap<>();
    private File jsonFile = new File("vehicles.json");
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public VehicleRepositoryImpl() {
        load();
    }

    @Override
    public boolean rentVehicle(String id) {
        Vehicle v = getVehicle(id);
        if (v == null) return false;
        if (v.isRented()) { 
            System.out.println("cannot rent this vehicle - already taken"); 
            return false; 
        }
        System.out.println("renting: " + v);
        v.setRented(true);
        vehicles.put(v.getId(), v);
        return true;
    }

    @Override
    public boolean returnVehicle(String id) {
        Vehicle v = getVehicle(id);
        if (v == null) return false;
        if (!v.isRented()) { 
            System.out.println("cannot return this vehicle - it's not rented"); 
            return false; 
        }
        System.out.println("returning: " + v);
        v.setRented(false);
        vehicles.put(v.getId(), v);
        return true;
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
        vehicles.put(vehicle.getId(), vehicle);
        return true;
    }

    @Override
    public boolean remove(String id) {
        vehicles.remove(id);
        return true;
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
                    obj.addProperty("category", ((Motorcycle) v).kategoria.toString());
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
                        
                        // Spróbuj pobrać kategorię z attributes
                        if (obj.has("attributes")) {
                            JsonObject attributes = obj.get("attributes").getAsJsonObject();
                            if (attributes.has("licence")) {
                                String licence = attributes.get("licence").getAsString();
                                try {
                                    cat = MotorcycleCategory.valueOf(licence);
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid motorcycle category: " + licence);
                                }
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
