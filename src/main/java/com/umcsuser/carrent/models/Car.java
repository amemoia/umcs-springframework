package com.umcsuser.carrent.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CAR")
public class Car extends Vehicle {

    public Car() {
    }

    @Override
    public Vehicle copy() {
        return new Car(getId(), brand, model, year, price, rented);
    }

    @Override
    public String toCSV() {
        return "CAR;"+super.toCSV();
    }


    public Car(String id, String brand, String model, int year, float price, boolean rented) {
        setId(id);
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = rented;
    }
}
