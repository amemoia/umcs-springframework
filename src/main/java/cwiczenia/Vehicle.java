package cwiczenia;

public abstract class Vehicle {

    public String brand;
    public String model;
    public int year;
    public float price;
    boolean rented;
    String id;

    public abstract Vehicle copy();

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

    public boolean isRented() {
        return rented;
    }

    public void setRented(boolean b) {
        rented = b;
    }
}
