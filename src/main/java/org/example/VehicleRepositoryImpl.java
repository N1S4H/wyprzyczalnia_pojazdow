package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VehicleRepositoryImpl implements IVehicleRepository {
    private List<Vehicle> vehicles = new ArrayList<>();
    private final String fileName = "vehicles.csv";

    public VehicleRepositoryImpl() throws FileNotFoundException {
        load();
    }

    @Override
    public boolean rentVehicle(String id) {
        for(Vehicle v : vehicles){
            if(v.getId().equals(id) && !v.isRented()){
                v.setRented(true);
                save();
                return true;
            }
        }
        System.out.println("Nie znaleziono pojazdu lub jest już wynajęty");
        return false;
    }

    @Override
    public boolean returnVehicle(String id) {
        for (Vehicle v : vehicles){
            if(v.getId().equals(id) && v.isRented()){
                v.setRented(false);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Vehicle> getVehicles() {
        List<Vehicle> copyList = new ArrayList<>();

        for (Vehicle v : vehicles){
            String className = v.getClass().getSimpleName();

            if(className.equals("Car")){
                copyList.add(new Car((Car) v));
            }else if(className.equals("Motorcycle")){
                copyList.add(new Motorcycle((Motorcycle) v));
            }
        }
        return copyList;
    }

    @Override
    public Vehicle getVehicle(String id) {
        for (Vehicle v : vehicles){
            if(v.getId().equals(id)){
                if (v instanceof Car) {
                    return new Car((Car) v);
                } else if (v instanceof Motorcycle) {
                    return new Motorcycle((Motorcycle) v);
                }
            }
        }
        return null;
    }

    @Override
    public boolean add(Vehicle vehicle) {
        if(vehicle == null){
            return false;
        }
        for(Vehicle v : vehicles){
            if(v.getId().equals(vehicle.getId())){
                System.out.println("Błąd: Pojazd o ID " + vehicle.getId() + " juz istnieje.");
                return false;
            }
        }

        vehicles.add(vehicle);
        save();
        return true;
    }

    @Override
    public boolean remove(String id) {
        boolean removed = vehicles.removeIf(v -> v.getId().equals(id));
        if(removed){
            save();
        }
        return removed;
    }

    @Override
    public void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))){
            for (Vehicle v : vehicles){
                writer.println(v.toCSV());
            }
        } catch (IOException e){
            System.out.println("Błąd zapisu do pliku: " + e.getMessage());
        }
    }

    @Override
    public void load() {
        vehicles.clear();
        File file = new File(fileName);

        System.out.println("DEBUG: Proba wczytania z: " + file.getAbsolutePath());

        if (!file.exists()){
            return;
        }

        try (Scanner scanner = new Scanner(file)){
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(";");
                String type = parts[0];

                String id = parts[1];
                String brand = parts[2];
                String model = parts[3];
                int year = Integer.parseInt(parts[4]);
                double price = Double.parseDouble(parts[5]);
                boolean rented = Boolean.parseBoolean(parts[6]);

                if (type.equalsIgnoreCase("MOTORCYCLE")){
                    String category = parts[7];
                    vehicles.add(new Motorcycle(id,brand, model, year, price, rented, category));
                } else if (type.equalsIgnoreCase("CAR")){
                    vehicles.add(new Car(id, brand, model, year, price, rented));
                }
            }
        }catch (Exception e){
            System.err.println("Błąd podczas wczytywania: " + e.getMessage());
        }
    }
}
