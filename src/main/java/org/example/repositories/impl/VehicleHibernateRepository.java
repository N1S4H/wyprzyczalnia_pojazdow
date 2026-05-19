package org.example.repositories.impl;

import org.example.db.HibernateConfig;
import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class VehicleHibernateRepository implements VehicleRepository {
    private Session session;

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public List<Vehicle> findAll() {
        if (session != null && session.isOpen()) {
            return session.createQuery("FROM Vehicle", Vehicle.class).list();
        }
        try (Session s = HibernateConfig.getSessionFactory().openSession()) {
            return s.createQuery("FROM Vehicle", Vehicle.class).list();
        }
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        if (session != null && session.isOpen()) {
            return Optional.ofNullable(session.get(Vehicle.class, id));
        }
        try (Session s = HibernateConfig.getSessionFactory().openSession()) {
            return Optional.ofNullable(s.get(Vehicle.class, id));
        }
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (session != null && session.isOpen()) {
            return session.merge(vehicle);
        }
        try (Session s = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            Vehicle merged = s.merge(vehicle);
            tx.commit();
            return merged;
        }
    }

    @Override
    public void deleteById(String id) {
        if (session != null && session.isOpen()) {
            Vehicle vehicle = session.get(Vehicle.class, id);
            if (vehicle != null) session.remove(vehicle);
            return;
        }
        try (Session s = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            Vehicle vehicle = s.get(Vehicle.class, id);
            if (vehicle != null) {
                s.remove(vehicle);
            }
            tx.commit();
        }
    }
}