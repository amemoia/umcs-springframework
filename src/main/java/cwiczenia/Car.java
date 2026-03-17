package cwiczenia;

public class Car extends Vehicle {

    @Override
    public Vehicle copy() {
        return new Car(id, brand, model, year, price, rented);
    }

    @Override
    public String toCSV() {
        return "CAR;"+super.toCSV();
    }


    Car(String id, String brand, String model, int year, float price, boolean rented) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = rented;
    }
}
