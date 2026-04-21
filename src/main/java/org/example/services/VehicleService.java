package org.example.services;

import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;
    private final VehicleValidator vehicleValidator;

    public VehicleService(VehicleRepository vehicleRepository, RentalRepository rentalRepository, VehicleValidator vehicleValidator) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.vehicleValidator = vehicleValidator;
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        if (vehicle.getId() == null) {
            vehicle.setId(UUID.randomUUID().toString());
        }
        vehicleValidator.validate(vehicle);

        vehicleRepository.save(vehicle);
        return vehicle;
    }

    public void removeVehicle(String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu."));

        boolean rented = rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        if (rented) {
            throw new IllegalStateException("Nie można usunąć pojazdu, bo jest aktualnie wypożyczony.");
        }

        vehicleRepository.deleteById(vehicle.getId());
    }

    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(v -> !isVehicleRented(v.getId()))
                .collect(Collectors.toList());
    }

    public boolean isVehicleRented(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }

    public Vehicle findById(String id) {
        return vehicleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Nie ma takiego pojazdu"));
    }
}
