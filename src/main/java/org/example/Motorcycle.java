package org.example;

public class Motorcycle extends Vehicle {
    private String category;

    public Motorcycle( String id, String brand, String model, int year, double price, boolean rented, String category){
        super(id, brand, model, year, price, rented);
        this.category = category;
    }

    public Motorcycle(Motorcycle other){
        super(other);
        this.category = other.category;
    }

    public String toCSV(){
        return "MOTORCYCLE;" + super.toCSV() + ";" + category;
    }

    @Override
    public String toString() {
        return super.toString() + " [Category: " + category + "]";
    }
}
