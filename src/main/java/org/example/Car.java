package org.example;

import org.example.Vehicle;

public class Car extends Vehicle {

    public Car(String id, String brand, String model, int year, double price, boolean rented){
        super(id, brand, model, year, price, rented);
    }

    public Car(Car other){
        super(other);
    }

    public String toCSV(){
        return "CAR;" + super.toCSV();
    }
}
