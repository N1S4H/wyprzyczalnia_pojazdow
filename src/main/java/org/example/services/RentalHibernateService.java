package org.example.services;

import lombok.experimental.NonFinal;
import org.example.db.HibernateConfig;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.impl.RentalHibernateRepository;
import org.example.repositories.impl.UserHibernateRepository;
import org.example.repositories.impl.VehicleHibernateRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.UUID;

public class RentalHibernateService implements RentalServiceInterface{

    private final RentalHibernateRepository rentalRepo;
    private final VehicleHibernateRepository vehicleRepo;
    private final UserHibernateRepository userRepo;

    public RentalHibernateService(RentalHibernateRepository rentalRepo, VehicleHibernateRepository vehicleRepo, UserHibernateRepository userRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        Transaction tx = null;

        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            setSession(session);

            boolean userHasActiveRental = rentalRepo.findAll().stream().anyMatch(r -> userId.equals(r.getUserId()) && r.isActive());
            if (userHasActiveRental) {
                throw new IllegalArgumentException("Masz już aktywne wypożyczenie.");
            }

            Vehicle vehicle = vehicleRepo.findById(vehicleId).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o podanym id."));

            User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono uzytkownika o podanym id."));

            boolean vehicleIsRented = rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicle.getId()).isPresent();

            if (vehicleIsRented) {
                throw new IllegalArgumentException("Ten pojazd jest juz wypozyczony.");
            }

            Rental rental = Rental.builder()
                    .id(UUID.randomUUID().toString())
                    .vehicle(vehicle)
                    .user(user)
                    .rentDateTime(LocalDateTime.now().toString())
                    .returnDateTime(null)
                    .build();

            Rental savedRental = rentalRepo.save(rental);
            tx.commit();
            return savedRental;
        } catch (RuntimeException e){
            rollback(tx);
            throw e;
        }
    }

    @Override
    public Rental returnVehicle(String userId) {
        Transaction tx = null;

        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            tx = session.beginTransaction();

            setSession(session);

            Rental rental = rentalRepo.findAll().stream()
                    .filter(r -> userId.equals(r.getUserId()))
                    .filter(Rental::isActive)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Nie masz aktualne wypozyczonego pojazdu."));

            rental.setReturnDateTime(LocalDateTime.now().toString());
            Rental savedRental = rentalRepo.save(rental);
            tx.commit();
            return savedRental;
        }catch(RuntimeException e){
            rollback(tx);
            throw e;
        }
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            setSession(session);

            return rentalRepo.findAll().stream()
                    .filter(r -> userId.equals(r.getUserId()))
                    .filter(Rental::isActive)
                    .findFirst();
        }
    }

    @Override
    public List<Rental> findAllRentals() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()){
            setSession(session);
            return rentalRepo.findAll();
        }
    }

    @Override
    public List<Rental> fingUserRentals(String userId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()){
            setSession(session);

            return rentalRepo.findAll().stream()
                    .filter(r -> userId.equals(r.getUserId()))
                    .toList();
        }
    }

    @Override
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()){
            setSession(session);

            return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        }
    }

    private void setSession(Session session) {
        rentalRepo.setSession(session);
        vehicleRepo.setSession(session);
        userRepo.setSession(session);
    }

    private void rollback(Transaction tx){
        if(tx != null && tx.isActive()){
            tx.rollback();
        }
    }
}
