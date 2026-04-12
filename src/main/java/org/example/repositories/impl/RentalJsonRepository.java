package org.example.repositories.impl;

import org.example.db.JsonFileStorage;
import org.example.models.Rental;
import org.example.repositories.RentalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RentalJsonRepository implements RentalRepository {
    private final JsonFileStorage<Rental> storage;
    private final List<Rental> rentals;

    public RentalJsonRepository(JsonFileStorage<Rental> storage) {
        this.storage = storage;
        List<Rental> loadedData = storage.load();
        this.rentals = (loadedData != null) ? loadedData : new ArrayList<>();
    }

    @Override
    public List<Rental> findAll() {
        return rentals.stream()
                .map(Rental::copy)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Rental> findById(String id) {
        return rentals.stream()
                .filter(r -> r.getId().equals(id))
                .map(Rental::copy)
                .findFirst();
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        return rentals.stream()
                .filter(r -> r.getVehicleId().equals(vehicleId) && r.isActive())
                .map(Rental::copy)
                .findFirst();
    }

    @Override
    public Rental save(Rental rental) {

        Optional<Rental> existingRental = rentals.stream()
                .filter(r -> r.getId().equals(rental.getId()))
                .findFirst();

        if (existingRental.isPresent()) {
            int index = rentals.indexOf(existingRental.get());
            rentals.set(index, rental.copy());
        } else {
            rentals.add(rental.copy());
        }

        storage.save(rentals);
        return rental;
    }

    @Override
    public void deleteById(String id) {
        if (rentals.removeIf(r -> r.getId().equals(id))) {
            storage.save(rentals);
        }
    }
}