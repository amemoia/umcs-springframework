package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.models.VehicleCategoryConfig;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class VehicleValidator {
    private final VehicleCategoryConfigService categoryConfigService;

    public VehicleValidator(VehicleCategoryConfigService categoryConfigService) {
        this.categoryConfigService = categoryConfigService;
    }

    public void validate(Vehicle vehicle) {
        if (vehicle == null) {
            throw new RuntimeException("VehicleValidator: vehicle cannot be null");
        }

        if (vehicle.getBrand() == null || vehicle.getBrand().isBlank()) {
            throw new RuntimeException("VehicleValidator: brand is required");
        }

        if (vehicle.getModel() == null || vehicle.getModel().isBlank()) {
            throw new RuntimeException("VehicleValidator: model is required");
        }

        if (vehicle.getYear() <= 1900) {
            throw new RuntimeException("VehicleValidator: year must be greater than 1900");
        }

        if (vehicle.getPrice() <= 0) {
            throw new RuntimeException("VehicleValidator: price must be positive");
        }

        VehicleCategoryConfig config = categoryConfigService.getByCategory(vehicle.getCategory());
        Map<String, String> requiredAttributes = config.getAttributes();
        Map<String, Object> actualAttributes = vehicle.getAttributes();

        for (String attrName : requiredAttributes.keySet()) {
            if (!actualAttributes.containsKey(attrName) || actualAttributes.get(attrName) == null) {
                throw new RuntimeException("Missing required attribute: " + attrName);
            }
        }
    }
}