package org.example.repositories.impl;

import org.example.db.JsonFileStorage;
import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VehicleJsonRepository implements VehicleRepository {
    private final JsonFileStorage<Vehicle> storage;
    private final List<Vehicle> vehicles;

    public VehicleJsonRepository(JsonFileStorage<Vehicle> storage) {
        this.storage = storage;
        List<Vehicle> loadedData = storage.load();
        this.vehicles = (loadedData != null) ? loadedData : new ArrayList<>();
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicles.stream()
                .map(Vehicle::copy)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicles.stream()
                .filter(v -> v.getId().equals(id))
                .map(Vehicle::copy)
                .findFirst();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        Optional<Vehicle> existingVehicle = vehicles.stream()
                .filter(v -> v.getId().equals(vehicle.getId()))
                .findFirst();

        if (existingVehicle.isPresent()) {
            int index = vehicles.indexOf(existingVehicle.get());
            vehicles.set(index, vehicle.copy());
        } else {
            vehicles.add(vehicle.copy());
        }

        storage.save(vehicles);
        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        if (vehicles.removeIf(v -> v.getId().equals(id))) {
            storage.save(vehicles);
        }
    }
}