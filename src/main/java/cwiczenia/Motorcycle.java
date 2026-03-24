package cwiczenia;

class Motorcycle extends Vehicle {
    MotorcycleCategory kategoria;

    @Override
    public String toCSV() {
        return "MOTORCYCLE;"+super.toCSV()+";"+kategoria;
    }

    @Override
    public Vehicle copy() {
        return new Motorcycle(id, brand, model, year, price, rented, kategoria);
    }

    public Motorcycle(String id, String brand, String model, int year, float price, boolean rented, MotorcycleCategory kategoria) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = rented;
        this.kategoria = kategoria;
    }
}