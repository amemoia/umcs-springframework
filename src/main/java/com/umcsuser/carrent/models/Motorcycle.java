package com.umcsuser.carrent.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
@DiscriminatorValue("MOTORCYCLE")
public class Motorcycle extends Vehicle {
    @Transient
    MotorcycleCategory kategoria;

    public Motorcycle() {
    }

    @Override
    public String toCSV() {
        return "MOTORCYCLE;"+super.toCSV()+";"+kategoria;
    }

    @Override
    public Vehicle copy() {
        return new Motorcycle(getId(), brand, model, year, price, rented, kategoria);
    }

    public Motorcycle(String id, String brand, String model, int year, float price, boolean rented, MotorcycleCategory kategoria) {
        setId(id);
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = rented;
        this.kategoria = kategoria;
    }

    public MotorcycleCategory getKategoria() {
        return kategoria;
    }

    public void setKategoria(MotorcycleCategory kategoria) {
        this.kategoria = kategoria;
    }
}