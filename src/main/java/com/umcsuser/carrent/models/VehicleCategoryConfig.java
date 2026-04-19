package com.umcsuser.carrent.models;

import java.util.Map;

public class VehicleCategoryConfig {
    private String category;
    private Map<String, String> attributes;

    public VehicleCategoryConfig(String category, Map<String, String> attributes) {
        this.category = category;
        this.attributes = attributes;
    }

    public String getCategory() {
        return category;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
}

