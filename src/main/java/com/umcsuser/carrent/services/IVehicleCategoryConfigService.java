package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.VehicleCategoryConfig;

import java.util.List;

public interface IVehicleCategoryConfigService {
    List<VehicleCategoryConfig> findAllCategories();
    VehicleCategoryConfig getByCategory(String category);
}

