package org.example.services;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RentalService implements RentalServiceInterface {
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public RentalService(RentalRepository rentalRepository, VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    public Rental rentVehicle(String userId, String vehicleId) {
        Vehicle foundVehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o podanym ID."));

        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika o podanym ID."));

        if (vehicleHasActiveRental(vehicleId)) {
            throw new IllegalStateException("Ten pojazd jest już wypożyczony przez kogoś innego.");
        }

        if (findActiveRentalByUserId(userId).isPresent()) {
            throw new IllegalStateException("Masz już aktywne wypożyczenie! Zwróć najpierw obecny pojazd.");
        }

        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .user(foundUser)
                .vehicle(foundVehicle)
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

        if (activeRental.getUser() != null) {
            userRepository.findById(activeRental.getUser().getId())
                    .ifPresent(activeRental::setUser);
        }

        if (activeRental.getVehicle() != null) {
            vehicleRepository.findById(activeRental.getVehicle().getId())
                    .ifPresent(activeRental::setVehicle);
        }

        return activeRental;
    }

    @Transactional(readOnly = true)
    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }

    @Transactional(readOnly = true)
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId) && r.getReturnDateTime() == null)
                .findFirst();
    }

    @Transactional(readOnly = true)
    public List<Rental> findUserRentals(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }
}