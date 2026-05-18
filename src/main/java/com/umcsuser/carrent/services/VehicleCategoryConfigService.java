package com.umcsuser.carrent.services;

import com.umcsuser.carrent.repositories.VehicleCategoryConfigRepository;

import java.util.List;

public class VehicleCategoryConfigService implements IVehicleCategoryConfigService {
    private final VehicleCategoryConfigRepository repository;

    public VehicleCategoryConfigService(VehicleCategoryConfigRepository repository) {
        this.repository = repository;
    }

    public List<com.umcsuser.carrent.models.VehicleCategoryConfig> findAllCategories() {
        return repository.findAll();
    }

    public com.umcsuser.carrent.models.VehicleCategoryConfig getByCategory(String category) {
        return repository.findByCategory(category)
                .orElseThrow(() -> new RuntimeException("category " + category + " does not exist"));
    }
}
