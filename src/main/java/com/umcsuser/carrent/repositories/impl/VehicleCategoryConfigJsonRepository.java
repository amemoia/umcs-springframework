package com.umcsuser.carrent.repositories.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.models.VehicleCategoryConfig;
import com.umcsuser.carrent.repositories.VehicleCategoryConfigRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VehicleCategoryConfigJsonRepository implements VehicleCategoryConfigRepository {
    private List<VehicleCategoryConfig> configs = new ArrayList<>();
    private final String filePath;
    private final ResourceLoader resourceLoader;

    public VehicleCategoryConfigJsonRepository(
            @Value("${carrent.json.categories-file}") String filePath,
            ResourceLoader resourceLoader) {
        this.filePath = filePath;
        this.resourceLoader = resourceLoader;
        load();
    }

    private void load() {
        try {
            Resource resource = resourceLoader.getResource(filePath.startsWith("classpath:") ? filePath : "file:" + filePath);
            if (!resource.exists() && !filePath.startsWith("classpath:") && !filePath.startsWith("file:")) {
                resource = resourceLoader.getResource("classpath:" + filePath);
            }

            if (!resource.exists()) {
                System.err.println("Categories file not found: " + filePath);
                configs = new ArrayList<>();
                return;
            }

            try (Reader reader = new InputStreamReader(resource.getInputStream())) {
                Type listType = new TypeToken<ArrayList<VehicleCategoryConfig>>() {}.getType();
                configs = new Gson().fromJson(reader, listType);
                if (configs == null) configs = new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Error loading categories from " + filePath + ": " + e.getMessage());
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