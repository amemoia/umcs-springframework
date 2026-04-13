package cwiczenia;

public abstract class Vehicle {
    protected String id;
    protected String brand;
    protected String model;
    protected int year;
    protected float price;
    protected boolean rented;

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
}
