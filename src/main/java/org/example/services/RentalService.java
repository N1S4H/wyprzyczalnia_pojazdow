package org.example.services;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RentalService implements RentalServiceInterface{
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;

    public RentalService(RentalRepository rentalRepository, VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Rental rentVehicle(String userId, String vehicleId) {
        vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o podanym ID."));

        if (vehicleHasActiveRental(vehicleId)) {
            throw new IllegalStateException("Ten pojazd jest już wypożyczony przez kogoś innego.");
        }

        if (findActiveRentalByUserId(userId).isPresent()) {
            throw new IllegalStateException("Masz już aktywne wypożyczenie! Zwróć najpierw obecny pojazd.");
        }

        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .user(User.builder().id(userId).build())
                .vehicle(Vehicle.builder().id(vehicleId).build())
                .rentDateTime(LocalDateTime.now().toString())
                .build();

        rentalRepository.save(rental);
        return rental;
    }

    public Rental returnVehicle(String userId) {
        Rental activeRental = findActiveRentalByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Nie posiadasz aktualnie żadnego wypożyczonego pojazdu."));

        activeRental.setReturnDateTime(LocalDateTime.now().toString());
        rentalRepository.save(activeRental);
        return activeRental;
    }

    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }

    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId) && r.getReturnDateTime() == null)
                .findFirst();
    }

    public List<Rental> findUserRentals(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }

    @Override
    public List<Rental> fingUserRentals(String userId) {
        return List.of();
    }

    @Override
    public boolean userHasActiveRental(String userId) {
        return false;
    }

}