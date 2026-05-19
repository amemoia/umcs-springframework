package com.umcsuser.carrent.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "category", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Car.class, name = "CAR"),
        @JsonSubTypes.Type(value = Motorcycle.class, name = "MOTORCYCLE")
})
@Entity
@Table(name = "vehicles")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "category")
public abstract class Vehicle {
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    protected UUID id;

    @Column(nullable = false)
    protected String brand;

    @Column(nullable = false)
    protected String model;

    @Column(nullable = false)
    protected int year;

    @Column(nullable = false)
    protected float price;

    @Column(nullable = false)
    protected boolean rented;

    @Column(name = "category", insertable = false, updatable = false)
    protected String category;

    @Column(name = "plate")
    protected String plate;

    @Transient
    protected Map<String, Object> attributes = new HashMap<>();

    protected Vehicle() {
    }

    public static VehicleBuilder builder() {
        return new VehicleBuilder();
    }

    public abstract Vehicle copy();

    public String getId() {
        return id != null ? id.toString() : null;
    }

    public void setId(String id) {
        this.id = id != null ? UUID.fromString(id) : null;
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
        if (plate != null && !attributes.containsKey("plate")) {
            attributes.put("plate", plate);
        }
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes != null ? new HashMap<>(attributes) : new HashMap<>();
        Object plateAttr = this.attributes.get("plate");
        if (plateAttr != null) {
            this.plate = plateAttr.toString();
        }
    }

    public void addAttribute(String name, Object value) {
        if (name != null && "plate".equalsIgnoreCase(name)) {
            this.plate = value != null ? value.toString() : null;
            return;
        }
        this.attributes.put(name, value);
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
        if (plate != null) {
            this.attributes.put("plate", plate);
        }
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
            String newId = (id == null || id.isBlank()) ? java.util.UUID.randomUUID().toString() : id;
            Vehicle vehicle;
            if ("CAR".equalsIgnoreCase(category)) {
                vehicle = new Car(newId, brand, model, year, price, false);
            } else if ("MOTORCYCLE".equalsIgnoreCase(category)) {
                vehicle = new Motorcycle(newId, brand, model, year, price, false, null);
            } else {
                vehicle = new Car(newId, brand, model, year, price, false);
            }
            vehicle.setCategory(category);
            if (plate != null) {
                vehicle.setPlate(plate);
            }
            return vehicle;
        }
    }
}
