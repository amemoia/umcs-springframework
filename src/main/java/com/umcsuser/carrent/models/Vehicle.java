package com.umcsuser.carrent.models;

import java.util.HashMap;
import java.util.Map;

public abstract class Vehicle {
    protected String id;
    protected String brand;
    protected String model;
    protected int year;
    protected float price;
    protected boolean rented;
    protected String category;
    protected Map<String, Object> attributes = new HashMap<>();

    public static VehicleBuilder builder() {
        return new VehicleBuilder();
    }

    public abstract Vehicle copy();

    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public float getPrice() {
        return price;
    }

    public boolean isRented() {
        return rented;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void addAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    public String toCSV() {
        return (
            id +
            ";" +
            brand +
            ";" +
            model +
            ";" +
            year +
            ";" +
            price +
            ";" +
            rented
        );
    }

    @Override
    public String toString() {
        return (
            "[" +
            id +
            "] $" +
            price +
            " " +
            (rented ? "rented" : "unrented") +
            " " +
            year +
            " " +
            brand +
            " " +
            model
        );
    }

    public static class VehicleBuilder {
        private String id;
        private String brand;
        private String model;
        private int year;
        private float price;
        private String plate;
        private String category;

        public VehicleBuilder id(String id) { this.id = id; return this; }
        public VehicleBuilder brand(String brand) { this.brand = brand; return this; }
        public VehicleBuilder model(String model) { this.model = model; return this; }
        public VehicleBuilder year(int year) { this.year = year; return this; }
        public VehicleBuilder price(double price) { this.price = (float) price; return this; }
        public VehicleBuilder plate(String plate) { this.plate = plate; return this; }
        public VehicleBuilder category(String category) { this.category = category; return this; }

        public Vehicle build() {
            Vehicle vehicle;
            if ("CAR".equalsIgnoreCase(category)) {
                vehicle = new Car(id, brand, model, year, price, false);
            } else if ("MOTORCYCLE".equalsIgnoreCase(category)) {
                vehicle = new Motorcycle(id, brand, model, year, price, false, null);
            } else {
                // Default to Car
                vehicle = new Car(id, brand, model, year, price, false);
            }
            vehicle.setCategory(category);
            if (plate != null) {
                vehicle.addAttribute("plate", plate);
            }
            return vehicle;
        }
    }
}
