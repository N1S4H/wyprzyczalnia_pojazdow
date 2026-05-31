package org.example.services;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RentalHibernateService implements RentalServiceInterface {

    private final RentalRepository rentalRepo;
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;

    public RentalHibernateService(RentalRepository rentalRepo, VehicleRepository vehicleRepo, UserRepository userRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        boolean userHasActiveRental = rentalRepo.findAll().stream()
                .anyMatch(r -> userId.equals(r.getUser().getId()) && r.getReturnDateTime() == null);
        if (userHasActiveRental) {
            throw new IllegalStateException("Masz już aktywne wypożyczenie.");
        }

        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o podanym id."));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono uzytkownika o podanym id."));

        boolean vehicleIsRented = rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicle.getId()).isPresent();

        if (vehicleIsRented) {
            throw new IllegalStateException("Ten pojazd jest juz wypozyczony.");
        }

        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .vehicle(vehicle)
                .user(user)
                .rentDateTime(LocalDateTime.now().toString())
                .returnDateTime(null)
                .build();

        return rentalRepo.save(rental);
    }

    @Override
    public Rental returnVehicle(String userId) {
        Rental rental = rentalRepo.findAll().stream()
                .filter(r -> userId.equals(r.getUser().getId()))
                .filter(r -> r.getReturnDateTime() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nie masz aktualnie wypozyczonego pojazdu."));

        rental.setReturnDateTime(LocalDateTime.now().toString());
        return rentalRepo.save(rental);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepo.findAll().stream()
                .filter(r -> userId.equals(r.getUser().getId()))
                .filter(r -> r.getReturnDateTime() == null)
                .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findAllRentals() {
        return rentalRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rental> findUserRentals(String userId) {
        return rentalRepo.findAll().stream()
                .filter(r -> userId.equals(r.getUser().getId()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }
}