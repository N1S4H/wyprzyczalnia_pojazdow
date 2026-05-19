package org.example.repositories.impl;

import org.example.db.HibernateConfig;
import org.example.models.Rental;
import org.example.repositories.RentalRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class RentalHibernateRepository implements RentalRepository {
    private Session session;

    public void setSession(Session session){
        this.session = session;
    }

    @Override
    public List<Rental> findAll() {
        if (session != null && session.isOpen()) {
            return session.createQuery("FROM Rental", Rental.class).list();
        }
        try (Session s = HibernateConfig.getSessionFactory().openSession()) {
            return s.createQuery("FROM Rental", Rental.class).list();
        }
    }

    @Override
    public Optional<Rental> findById(String id) {
        if (session != null && session.isOpen()) {
            return Optional.ofNullable(session.get(Rental.class, id));
        }
        try (Session s = HibernateConfig.getSessionFactory().openSession()) {
            return Optional.ofNullable(s.get(Rental.class, id));
        }
    }

    @Override
    public Rental save(Rental rental) {
        if (session != null && session.isOpen()) {
            return session.merge(rental);
        }
        try (Session s = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            Rental merged = s.merge(rental);
            tx.commit();
            return merged;
        }
    }

    @Override
    public void deleteById(String id) {
        if (session != null && session.isOpen()) {
            Rental rental = session.get(Rental.class, id);
            if(rental != null) session.remove(rental);
            return;
        }
        try (Session s = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            Rental rental = s.get(Rental.class, id);
            if (rental != null) {
                s.remove(rental);
            }
            tx.commit();
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String hql = "FROM Rental r WHERE r.vehicle.id = :vehicleId AND r.returnDateTime IS NULL";
        if (session != null && session.isOpen()) {
            Query<Rental> query = session.createQuery(hql, Rental.class);
            query.setParameter("vehicleId", vehicleId);
            return query.uniqueResultOptional();
        }
        try (Session s = HibernateConfig.getSessionFactory().openSession()) {
            Query<Rental> query = s.createQuery(hql, Rental.class);
            query.setParameter("vehicleId", vehicleId);
            return query.uniqueResultOptional();
        }
    }
}