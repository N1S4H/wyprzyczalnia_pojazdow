package org.example.repositories;

import org.example.models.Vehicle;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

@Profile("jpa")
public interface VehicleJpaRepository extends JpaRepository<Vehicle, String> {
}