package com.umcsuser.carrent.repositories.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.models.VehicleCategoryConfig;
import com.umcsuser.carrent.repositories.VehicleCategoryConfigRepository;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleCategoryConfigJsonRepository implements VehicleCategoryConfigRepository {
    private List<VehicleCategoryConfig> configs = new ArrayList<>();

    public VehicleCategoryConfigJsonRepository() {
        load();
    }

    private void load() {
        try (FileReader reader = new FileReader("categories.json")) {
            Type listType = new TypeToken<ArrayList<VehicleCategoryConfig>>() {}.getType();
            configs = new Gson().fromJson(reader, listType);
            if (configs == null) configs = new ArrayList<>();
        } catch (IOException e) {
            configs = new ArrayList<>();
        }
    }

    @Override
    public List<VehicleCategoryConfig> findAll() {
        return configs;
    }

    @Override
    public Optional<VehicleCategoryConfig> findByCategory(String category) {
        return configs.stream().filter(c -> c.getCategory().equalsIgnoreCase(category)).findFirst();
    }
}

