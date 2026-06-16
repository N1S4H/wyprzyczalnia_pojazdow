package org.example.repositories.impl;

import org.example.models.Vehicle;
import org.example.repositories.VehicleJpaRepository;
import org.example.repositories.VehicleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public class VehicleRepositoryJpaAdapter implements VehicleRepository {

    private final VehicleJpaRepository delegate;

    public VehicleRepositoryJpaAdapter(VehicleJpaRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Vehicle> findAll() {
        return delegate.findAll();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return delegate.findById(id);
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null) {
            vehicle.setId(UUID.randomUUID().toString());
        }
        return delegate.save(vehicle);
    }

    @Override
    public void deleteById(String id) {
        delegate.deleteById(id);
    }
}