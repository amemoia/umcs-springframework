package com.umcsuser.carrent.services;

public class VehicleValidator {
    private final VehicleCategoryConfigService categoryConfigService;

    public VehicleValidator(VehicleCategoryConfigService categoryConfigService) {
        this.categoryConfigService = categoryConfigService;
    }
}

