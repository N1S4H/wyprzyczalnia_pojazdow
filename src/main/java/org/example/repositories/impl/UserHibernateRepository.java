package org.example.repositories.impl;

import org.example.db.HibernateConfig;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class UserHibernateRepository implements UserRepository {
    private Session session;

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public List<User> findAll() {
        if (session != null && session.isOpen()) {
            return session.createQuery("FROM User", User.class).list();
        }
        try (Session s = HibernateConfig.getSessionFactory().openSession()) {
            return s.createQuery("FROM User", User.class).list();
        }
    }

    @Override
    public Optional<User> findById(String id) {
        if (session != null && session.isOpen()) {
            return Optional.ofNullable(session.get(User.class, id));
        }
        try (Session s = HibernateConfig.getSessionFactory().openSession()) {
            return Optional.ofNullable(s.get(User.class, id));
        }
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String hql = "FROM User u WHERE u.login = :login";
        if (session != null && session.isOpen()) {
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("login", login);
            return query.uniqueResultOptional();
        }
        try (Session s = HibernateConfig.getSessionFactory().openSession()) {
            Query<User> query = s.createQuery(hql, User.class);
            query.setParameter("login", login);
            return query.uniqueResultOptional();
        }
    }

    @Override
    public User save(User user) {
        if (session != null && session.isOpen()) {
            return session.merge(user);
        }
        try (Session s = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            User merged = s.merge(user);
            tx.commit();
            return merged;
        }
    }

    @Override
    public void deleteById(String id) {
        if (session != null && session.isOpen()) {
            User user = session.get(User.class, id);
            if (user != null) session.remove(user);
            return;
        }
        try (Session s = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            User user = s.get(User.class, id);
            if (user != null) {
                s.remove(user);
            }
            tx.commit();
        }
    }
}