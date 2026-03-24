package org.example;

public class Vehicle {
    private String brand;
    private String model;
    private int year;
    private double price;
    private boolean rented;
    private String id;

    public String getBrand(){
        return brand;
    }

    public String getModel(){
        return model;
    }

    public int getYear(){
        return year;
    }

    public double getPrice(){
        return price;
    }

    public boolean isRented(){
        return rented;
    }

    public String getId(){
        return id;
    }

    public void setBrand(String brand){
        this.brand = brand;
    }

    public void setModel(String model){
        this.model = model;
    }

    public void setYear(int year){
        this.year = year;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public void setId(String id){
        this.id = id;
    }

    public Vehicle(String id, String brand, String model, int year, double price, boolean rented){
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = rented;
        this.id = id;
    }

    public Vehicle(Vehicle other){
        this.brand = other.brand;
        this.model = other.model;
        this.year = other.year;
        this.price = other.price;
        this.rented = other.rented;
        this.id = other.id;
    }

    public String toCSV(){
        return id + ";" + brand + ";" + model + ";" + year + ";" + price + ";" + rented;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", price=" + price +
                ", rented=" + rented +
                ", id=" + id +
                '}';
    }


}
