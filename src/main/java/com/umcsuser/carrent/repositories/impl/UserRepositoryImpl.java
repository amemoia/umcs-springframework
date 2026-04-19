package com.umcsuser.carrent.repositories.impl;

import com.google.gson.*;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Implementacja UserRepository - obsługuje przechowywanie użytkowników w JSON
 */
public class UserRepositoryImpl implements UserRepository {
    private Map<String, User> users = new HashMap<>();
    private File jsonFile = new File("users.json");
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public UserRepositoryImpl() {
        load();
    }

    @Override
    public User getUser(String login) {
        return users.get(login);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(User user) {
        users.put(user.getLogin(), user);
        save();
        return user;
    }

    @Override
    public boolean registerNewUser(String login, String password, String role) {
        if (users.containsKey(login)) {
            return false;
        }
        User newUser = new User(login, password, role);
        users.put(login, newUser);
        return true;
    }

    @Override
    public int removeUser(String login, User admin) {
        if (admin.getRole() != com.umcsuser.carrent.models.Role.ADMIN) {
            return 1;
        }

        User toRemove = users.get(login);
        if (toRemove == null) {
            return 1;
        }

        if (toRemove.getRentedVehicleId() != null && !toRemove.getRentedVehicleId().isEmpty()) {
            return 2;
        }

        users.remove(login);
        return 0;
    }

    @Override
    public void save() {
        try {
            JsonArray array = new JsonArray();
            for (User u : users.values()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", u.getId());
                obj.addProperty("login", u.getLogin());
                obj.addProperty("passwordHash", u.getPassword());
                obj.addProperty("role", u.getRole().name());
                if (u.getRentedVehicleId() != null) {
                    obj.addProperty("rentedVehicleId", u.getRentedVehicleId());
                }
                array.add(obj);
            }
            
            String json = gson.toJson(array);
            Files.write(Paths.get(jsonFile.getPath()), json.getBytes());
            System.out.println("user repo save successful");
        } catch (IOException e) {
            System.out.println("error saving users!");
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        try {
            if (!jsonFile.exists()) {
                System.out.println("users.json not found, starting with empty users");
                return;
            }

            String json = new String(Files.readAllBytes(Paths.get(jsonFile.getPath())));
            JsonArray array = gson.fromJson(json, JsonArray.class);
            
            if (array != null) {
                for (JsonElement element : array) {
                    JsonObject obj = element.getAsJsonObject();
                    
                    // Bezpieczne odczytywanie pól
                    if (!obj.has("login")) {
                        System.out.println("Skipping invalid user entry: missing login");
                        continue;
                    }
                    
                    String login = obj.get("login").getAsString();
                    // Obsługa "passwordHash" lub "password"
                    String password = obj.has("passwordHash") 
                        ? obj.get("passwordHash").getAsString()
                        : (obj.has("password") ? obj.get("password").getAsString() : "");
                    String role = obj.has("role") ? obj.get("role").getAsString() : "USER";
                    String rentedVehicleId = obj.has("rentedVehicleId") && !obj.get("rentedVehicleId").isJsonNull()
                        ? obj.get("rentedVehicleId").getAsString()
                        : null;
                    
                    User user = new User(login, password, role);
                    if (rentedVehicleId != null) {
                        user.setRentedVehicleId(rentedVehicleId);
                    }
                    users.put(login, user);
                }
            }
        } catch (IOException e) {
            System.out.println("error loading users!");
            e.printStackTrace();
        }
    }
}
